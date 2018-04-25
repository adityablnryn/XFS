package peer;

import java.io.Serializable;

public class FileDownloadBundle implements Serializable {
    public String fileName;
    public byte[] fileContents;
    public long checksum;
}
