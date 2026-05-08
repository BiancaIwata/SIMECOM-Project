package school.sptech;

import java.sql.Connection;

public class ProcessorContext implements AutoCloseable {

    private final Connection connection;
    private final S3Service s3Service;
    private final String s3Prefix;

    public ProcessorContext(Connection connection, S3Service s3Service, String s3Prefix) {
        this.connection = connection;
        this.s3Service = s3Service;
        this.s3Prefix = s3Prefix;
    }

    public Connection getConnection() {
        return connection;
    }

    public S3Service getS3Service() {
        return s3Service;
    }

    public String getS3Prefix() {
        return s3Prefix;
    }

    @Override
    public void close() {
        DatabaseConnection.close(connection);
        if (s3Service != null) {
            s3Service.close();
        }
    }
}
