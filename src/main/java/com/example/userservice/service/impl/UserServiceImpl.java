package com.example.userservice.service.impl;
/*
 *  @author diemdz
 */


import com.example.userservice.entity.UserEntity;
import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.model.response.PostUserListResponseBody;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<PostUserListResponseBody> postUserList(PostUserListRequestBody requestBody) {
        Specification<UserEntity> employeeInfoEntitySpec = getUser(requestBody);
        List<UserEntity> userEntityList = userRepository.findAll(employeeInfoEntitySpec);
        return userEntityList.stream().map((e)->
              modelMapper.map(e, PostUserListResponseBody.class)
        ).toList();
    }
    private Specification<UserEntity> getUser(PostUserListRequestBody requestBody) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(requestBody.getUserName())) {
                predicates.add(builder.like(root.get("userName"), "%" + requestBody.getUserName() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
    @Override
    public boolean insertBatchProducts() {
        log.info("{} insertBatchProducts {}", getClass().getSimpleName());
        int batchSize = 100000; // Số bản ghi mỗi batch
        List<UserEntity> users = new ArrayList<>();

        for (int i = 0; i < 1_000_000; i++) {
            UserEntity product = new UserEntity();
            product.setId(UUID.randomUUID().toString());
            product.setUserName("username " + i);
            product.setPassword("pass " + i);

            users.add(product);

            if (users.size() == batchSize) {
                userRepository.saveAll(users); // Insert batch
                users.clear(); // Clear list để tiết kiệm bộ nhớ
            }
        }

        if (!users.isEmpty()) {
            userRepository.saveAll(users); // Insert các bản ghi còn lại
        }
        return true;
    }

    @Override
    public boolean insertMultiThreadProducts(int records, int threadCount) {
        log.info("{} insertBatchProducts using multithreading", getClass().getSimpleName());

//        int totalRecords = 1_000_000;
//        int threadCount = 10; // Số lượng luồng
        int recordsPerThread = records / threadCount; // Số bản ghi mỗi luồng xử lý
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            int start = threadIndex * recordsPerThread;
            int end = (threadIndex + 1) * recordsPerThread;

            tasks.add(() -> {
                List<UserEntity> users = new ArrayList<>();
                for (int i = start; i < end; i++) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(UUID.randomUUID().toString());
                    userEntity.setUserName("username " + i);
                    userEntity.setPassword("pass " + i);

                    users.add(userEntity);

                    // Batch insert
                    if (users.size() == 1_000) {
                        userRepository.saveAll(users);
                        users.clear();
                    }
                }

                // Insert các bản ghi còn lại
                if (!users.isEmpty()) {
                    userRepository.saveAll(users);
                }

                return true;
            });
        }

        try {
            // Thực thi các task song song
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Error executing tasks: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } finally {
            executorService.shutdown();
        }

        log.info("All tasks completed successfully");
        return true;
    }
    @Override
    public ResponseEntity<byte[]> export() throws IOException {
        log.info("{} exportUsers {}", getClass().getSimpleName());
        List<UserEntity> users = userRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("User");

            // Header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Username");
            headerRow.createCell(2).setCellValue("Pass");


            // Data rows
            for (int i = 0; i < users.size(); i++) {
                UserEntity userEntity = users.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(userEntity.getId());
                row.createCell(1).setCellValue(userEntity.getUserName());
                row.createCell(2).setCellValue(userEntity.getPassword());
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "user.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray());

        }
    }

    public List<UserEntity> fetchAllUsers(int batchSize, int threadCount) throws InterruptedException, ExecutionException {
        int totalProducts = (int) userRepository.count();  // Lấy tổng số bản ghi
        int totalPages = (int) Math.ceil((double) totalProducts / batchSize);

        List<Future<List<UserEntity>>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);  // Dùng 10 luồng xử lý

        for (int page = 0; page < totalPages; page++) {
            final int pageIndex = page;
            Callable<List<UserEntity>> task = () -> {
                Page<UserEntity> productPage = userRepository.findAll(PageRequest.of(pageIndex, batchSize));
                return productPage.getContent();  // Trả về danh sách sản phẩm trong trang hiện tại
            };
            futures.add(executorService.submit(task));
        }

        // Kết hợp kết quả từ các luồng
        List<UserEntity> allProducts = new ArrayList<>();
        for (Future<List<UserEntity>> future : futures) {
            allProducts.addAll(future.get());
        }

        executorService.shutdown();  // Đóng ExecutorService

        return allProducts;  // Trả về danh sách tất cả các sản phẩm
    }
    @Override
    public ResponseEntity<byte[]> exportMultiThread(int batchSize,int threadCount) throws IOException, InterruptedException, ExecutionException {
        log.info("{} exportMultiThreadProducts {}", getClass().getSimpleName());
        List<UserEntity> users = fetchAllUsers(batchSize, threadCount);

        log.info("{} exportMultiThreadProducts totalRecords {}", getClass().getSimpleName(),users.size());
        // Khởi tạo workbook với SXSSF (tối ưu bộ nhớ khi làm việc với dữ liệu lớn)
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Projects");

        // Tiêu đề của bảng Excel
        Row headerRow = sheet.createRow(0);
//        String[] columns = {"Id", "Name", "Category", "Quantity", "Inventory", "Material", "PriceList", "Discount"};
        String[] columns = {"Id", "Username", "Pass"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Executor để xử lý đa luồng
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 10 luồng
        List<Callable<Void>> tasks = new ArrayList<>();

        int batchCount = (int) Math.ceil((double) users.size() / batchSize);
        for (int i = 0; i < batchCount; i++) {
            final int batchIndex = i;
            tasks.add(() -> {
                int start = batchIndex * batchSize;
                int end = Math.min(start + batchSize, users.size());

                // Ghi dữ liệu vào Excel cho batch này
                synchronized (workbook) {
                    int rowNum = start + 1;
                    for (int j = start; j < end; j++) {
                        UserEntity product = users.get(j);
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(product.getId());
                        row.createCell(1).setCellValue(product.getUserName());
                        row.createCell(2).setCellValue(product.getPassword());
                    }
                }
                return null;
            });
        }

        // Thực thi các task song song
        executorService.invokeAll(tasks);

        // Tạo ByteArrayOutputStream để lưu trữ file Excel
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);

        // Giải phóng bộ nhớ
        workbook.dispose();
        executorService.shutdown();

        // Trả về file Excel cho người dùng
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "export_projects; filename=export_projects.xlsx");
        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

}
