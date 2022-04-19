package com.celonis.challenge.tasks.files;

import com.celonis.challenge.persistence.adapter.CounterPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class FileService {

    private final CounterPersistenceAdapter repository;

    public void executeTask(String taskId) throws SchedulerException {
//        Task task = persistenceAdapter.getTask(taskId);
//        CounterTaskJob counterTaskJob = mapper.toCounterTaskJob(task);
//
//        if (true) {
//
//            timerService.runTimerJob(counterTaskJob);
//
//        } else {
//            URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
//            if (url == null) {
//                throw new InternalException("Zip file not found");
//            }
//            try {
//                fileService.storeResult(taskId, url);
//            } catch (Exception e) {
//                throw new InternalException(e);
//            }
//        }
    }

    public ResponseEntity<FileSystemResource> getTaskResult(String taskId) {
//        Task task = repository
//                .getTask(taskId);
//        File inputFile = new File(task.getStorageLocation());
//
//        if (!inputFile.exists()) {
//            throw new InternalException("File not generated yet");
//        }
//
//        HttpHeaders respHeaders = new HttpHeaders();
//        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");
//
//        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
        return null;
    }

    public void storeResult(String taskId, URL url) throws IOException {
//        Task task = repository
//                .getTask(taskId);
//        File outputFile = File.createTempFile(taskId, ".zip");
//        outputFile.deleteOnExit();
//        task.setStorageLocation(outputFile.getAbsolutePath());
//        repository.createTask(task);
//        try (InputStream is = url.openStream();
//             OutputStream os = new FileOutputStream(outputFile)) {
//            IOUtils.copy(is, os);
//        }
    }

}
