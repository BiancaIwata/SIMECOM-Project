package school.sptech;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de integração com AWS S3 para leitura de arquivos de dados do MDIC.
 *
 * Conecta ao bucket configurado, lista objetos disponíveis e faz download
 * para processamento local via Apache POI + JDBC.
 *
 * Configuração via variáveis de ambiente:
 *   - S3_BUCKET_NAME           → nome do bucket (padrão: simecom-s3)
 *   - AWS_REGION               → região AWS (padrão: us-east-1)
 *   - AWS_ACCESS_KEY_ID        → chave de acesso AWS
 *   - AWS_SECRET_ACCESS_KEY    → chave secreta AWS
 *     (ou use ~/.aws/credentials, IAM role, SSO, etc.)
 */
public class S3Service implements AutoCloseable {

    private final S3Client s3;
    private final String bucketName;

    //  CONFIGURAÇÃO PADRÃO 
    private static final String DEFAULT_BUCKET = "simecom-bucket-s3";
    private static final Region DEFAULT_REGION = Region.US_EAST_1;

    //  CONSTRUTORES

    /*  
        Construtor sem argumentos — usa variáveis de ambiente ou valores padrão para bucket e região.
    */
    public S3Service() {
        this(
            System.getenv().getOrDefault("S3_BUCKET_NAME", DEFAULT_BUCKET),
            Region.of(System.getenv().getOrDefault("AWS_REGION", DEFAULT_REGION.id()))
        );
    }

    /**
     * Cria o serviço S3 com bucket e região explícitos.
     */
    public S3Service(String bucketName, Region region) {
        this.bucketName = bucketName;
        this.s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        System.out.printf("[S3] Conectado — bucket: '%s', região: %s%n", bucketName, region);
    }

    //  LISTAR OBJETOS NO BUCKET

    /**
     * Lista todos os objetos (keys) no bucket, opcionalmente filtrados por prefixo.
     * Suporta paginação automática para buckets com muitos objetos.
     *
     * @param prefix Prefixo/pasta no bucket (ex: "dados/" ou "" para tudo)
     * @return Lista de keys (caminhos completos dentro do bucket)
     */
    public List<String> listarArquivos(String prefix) {
        List<String> keys = new ArrayList<>();
        String continuationToken = null;
        boolean hasMore = true;

        while (hasMore) {
            ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                    .bucket(bucketName);

            if (prefix != null && !prefix.isEmpty()) {
                builder.prefix(prefix);
            }
            if (continuationToken != null) {
                builder.continuationToken(continuationToken);
            }

            ListObjectsV2Response response = s3.listObjectsV2(builder.build());

            for (S3Object obj : response.contents()) {
                // Ignorar "pastas" virtuais e objetos vazios
                if (!obj.key().endsWith("/") && obj.size() > 0) {
                    keys.add(obj.key());
                }
            }

            hasMore = response.isTruncated() != null && response.isTruncated();
            continuationToken = response.nextContinuationToken();
        }

        System.out.printf("[S3] %d objetos encontrados no bucket '%s'%s%n",
                keys.size(), bucketName,
                (prefix != null && !prefix.isEmpty()) ? " (prefixo: " + prefix + ")" : "");

        return keys;
    }

    //  DOWNLOAD DE OBJETO

    /**
     * Download de um objeto S3 para um arquivo local.
     *
     * @param key     Chave (caminho) do objeto no bucket
     * @param destino Caminho local de destino
     * @return O Path do arquivo baixado
     */
    public Path download(String key, Path destino) throws Exception {
        System.out.printf("[S3] Baixando: s3://%s/%s%n", bucketName, key);

        // Criar diretório de destino se necessário
        Files.createDirectories(destino.getParent());

        // Apagar arquivo anterior se existir (evita FileAlreadyExistsException)
        Files.deleteIfExists(destino);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.getObject(request, destino);

        long tamanho = Files.size(destino);
        System.out.printf("[S3] Download concluído: %s (%.2f MB)%n",
                destino.getFileName(), tamanho / (1024.0 * 1024.0));

        return destino;
    }

    //  UTILITÁRIOS

    public String getBucketName() {
        return bucketName;
    }

    @Override
    public void close() {
        if (s3 != null) {
            s3.close();
            System.out.println("[S3] Conexão encerrada.");
        }
    }
}
