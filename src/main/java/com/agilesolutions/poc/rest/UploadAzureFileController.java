package com.agilesolutions.poc.rest;

import com.azure.spring.cloud.core.resource.AzureStorageFileProtocolResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.agilesolutions.poc.storage.SampleDataInitializer.FILE_RESOURCE_PATTERN;

@RequestMapping("file")
@RequiredArgsConstructor
@Slf4j
public class UploadAzureFileController {

    private final String shareName;
    private final ResourceLoader resourceLoader;
    private final AzureStorageFileProtocolResolver azureStorageFileProtocolResolver;

    /**
     * Using AzureStorageFileProtocolResolver to get Azure Storage File Share resources with file pattern.
     *
     * @return fileNames in the container match pattern: *.txt
     */
    @GetMapping
    public List<String> listTxtFiles() throws IOException {
        Resource[] resources = azureStorageFileProtocolResolver.getResources(String.format(FILE_RESOURCE_PATTERN, this.shareName, "*.txt"));
        log.info("{} resources founded with pattern:*.txt",resources.length);
        return Stream.of(resources).map(Resource::getFilename).collect(Collectors.toList());
    }

    @GetMapping("/{fileName}")
    public String readResource(@PathVariable("fileName") String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(String.format(FILE_RESOURCE_PATTERN, this.shareName, fileName));
        return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
    }


    @PostMapping("/{fileName}")
    public String writeResource(@PathVariable("fileName") String fileName, @RequestBody String data) throws IOException {
        Resource resource = resourceLoader.getResource(String.format(FILE_RESOURCE_PATTERN, this.shareName, fileName));
        try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
            os.write(data.getBytes());
        }
        return "file was updated";
    }


}
