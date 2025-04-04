package top.alazeprt.pclib.util;

import org.junit.jupiter.api.Test;

import java.io.File;

public class MultiThreadDownloadTest {
    @Test
    public void testDownload() throws Exception {
        MultiThreadDownloader.download("https://api.spiget.org/v2/resources/83767/download", 4, new File("./"));
    }
}
