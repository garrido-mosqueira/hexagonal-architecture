package com.celonis.challenge.tasks.files.adapter;

import com.celonis.challenge.domain.exceptions.InternalException;
import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.domain.port.*;
import com.celonis.challenge.persistence.adapter.FileTaskPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileTaskAdapter implements CreateFileTaskPort, ReadFileTaskPort, DeleteFileTaskPort, UpdateFileTaskPort, ExecuteCounterTaskPort {

    private final FileTaskPersistenceAdapter persistenceAdapter;

    @Override
    public FileTask createTask(FileTask counterTask) {
        return persistenceAdapter.createTask(counterTask);
    }

    @Override
    public List<FileTask> getTasks() {
        return persistenceAdapter.getTasks();
    }

    @Override
    public FileTask getTask(String taskId) {
        return persistenceAdapter.getTask(taskId);
    }

    @Override
    public void deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
    }

    @Override
    public FileTask updateTask(String taskId, FileTask counterTaskUpdate) {
        return persistenceAdapter.updateTask(taskId, counterTaskUpdate);
    }

    @SneakyThrows
    @Override
    public void executeTask(String taskId) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            storeResult(taskId, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public File getTaskResult(String taskId) {
        FileTask task = persistenceAdapter.getTask(taskId);
        File inputFile = new File(task.getStorageLocation());

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }
        return inputFile;
    }

    public void storeResult(String taskId, URL url) throws IOException {
        FileTask task = persistenceAdapter.getTask(taskId);
        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();
        task.setStorageLocation(outputFile.getAbsolutePath());
        persistenceAdapter.createTask(task);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }

}
