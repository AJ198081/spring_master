package dev.aj.full_stack_v5;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.boot.test.context.TestComponent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@TestComponent
@RequiredArgsConstructor
public class PhotosFactory {

    private static Set<String> availablePhotos;
    public static final String RELATIVE_PHOTOS_DIRECTORY_PATH = "src/test/resources/photos";

    private final Faker faker;

    @PostConstruct
    public void init() {

        Path currentDirectory = Path.of(System.getProperty("user.dir"));

        if (!currentDirectory.endsWith(Paths.get("full_stack_v5"))) {
            currentDirectory = currentDirectory.resolve("full_stack_v5");
        }

        Path absolutePhotosPath = currentDirectory.resolve(RELATIVE_PHOTOS_DIRECTORY_PATH);

        File photosDirectory = new File(absolutePhotosPath.toUri());

        if (!photosDirectory.exists()) {
            throw new RuntimeException("Photos directory path [%s] does not exist".formatted(photosDirectory.getAbsolutePath()));
        }

        IOFileFilter pngFileFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".png"));

        availablePhotos = Arrays.stream(Objects.requireNonNull(photosDirectory.listFiles()))
                .filter(pngFileFilter::accept)
                .map(File::getName)
                .collect(Collectors.toSet());

        FileAlterationObserver photoDirectoryObserver = new FileAlterationObserver(photosDirectory);
        photoDirectoryObserver.addListener(new PhotoFileListener());
        FileAlterationMonitor photoDirectoryMonitor = new FileAlterationMonitor(10000, photoDirectoryObserver);

        try {
            photoDirectoryMonitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class PhotoFileListener extends FileAlterationListenerAdaptor {
        @Override
        public void onFileChange(File file) {
            String photoName = file.getName();
            availablePhotos.remove(photoName);
            availablePhotos.add(photoName);
        }

        @Override
        public void onFileCreate(File file) {
            availablePhotos.add(file.getName());
        }

        @Override
        public void onFileDelete(File file) {
            availablePhotos.remove(file.getName());
        }
    }

    public String getRandomPhoto() {
        return availablePhotos.stream()
                .skip(faker.random().nextInt(availablePhotos.size()))
                .findFirst()
                .orElse(null);
    }
}
