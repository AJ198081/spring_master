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
    public static final String ABSOLUTE_PHOTOS_DIRECTORY_PATH = "C:/Users/amarj/IdeaProjects/spring_master/full_stack_v4/src/test/resources/photos";

    private final Faker faker;


    public String getRandomPhoto() {
        return availablePhotos.stream()
                .skip(faker.random().nextInt(availablePhotos.size()))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void init() {
        Path pathToPhotosDirectory = Paths.get(ABSOLUTE_PHOTOS_DIRECTORY_PATH);

        File photosDirectory = new File(pathToPhotosDirectory.toUri());

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


}
