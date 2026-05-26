package school.sptech.comex.service;

import school.sptech.comex.model.ChartFiles;
import school.sptech.comex.model.FilterRequest;
import school.sptech.comex.model.MetricsResult;
import school.sptech.comex.model.ReportContext;
import school.sptech.comex.util.FileUtils;
import school.sptech.comex.util.NumberUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfReportService {

    private static final float MARGIN = 50f;
    private static final float TITLE_FONT_SIZE = 18f;
    private static final float SUBTITLE_FONT_SIZE = 13f;
    private static final float BODY_FONT_SIZE = 11f;
    private static final float LINE_SPACING = 1.35f;
    private static final float IMAGE_MAX_WIDTH = 500f;
    private static final float IMAGE_MAX_HEIGHT = 300f;

    private final PDFont titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDFont subtitleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDFont bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    public Path generate(ReportContext context) throws Exception {
        FilterRequest filter = context.getFilterRequest();
        MetricsResult metrics = context.getMetricsResult();
        ChartFiles charts = context.getChartFiles();

        Path outDir = FileUtils.ensureDirectory(filter.getOutputDirectory());
        Path pdfPath = outDir.resolve("relatorio_comex.pdf");

        try (PDDocument document = new PDDocument()) {
            PdfWriter writer = new PdfWriter(document);

            writer.writeTitle(filter.getReportTitle());
            writer.writeBlankLine();
            writer.writeLine("Tipo: " + valueOrDash(filter.getTradeType()));
            writer.writeLine("Período: " + valueOrDash(filter.getYearStart()) + " até " + valueOrDash(filter.getYearEnd()));
            writer.writeLine("Meses: " + valueOrDash(filter.getMonthStart()) + " até " + valueOrDash(filter.getMonthEnd()));
            writer.writeLine("SH4: " + joinList(filter.getSh4List()));
            writer.writeLine("Países: " + joinList(filter.getCountryList()));
            writer.writeLine("UFs: " + joinList(filter.getUfList()));
            writer.writeLine("Municípios: " + joinList(filter.getMunicipalityList()));
            writer.writeLine("Total de registros filtrados: " + safeSize(context));
            writer.writeLine("Texto analítico: " + (context.isUsedAi() ? "IA" : "Fallback local"));
            writer.writeBlankLine();

            writer.writeSectionTitle("Métricas principais");
            writer.writeLine("VL_FOB Importação: " + NumberUtils.formatMoney(metrics.getTotalVlfobImport()));
            writer.writeLine("VL_FOB Exportação: " + NumberUtils.formatMoney(metrics.getTotalVlfobExport()));
            writer.writeLine("KG Importação: " + NumberUtils.formatMoney(metrics.getTotalKgImport()));
            writer.writeLine("KG Exportação: " + NumberUtils.formatMoney(metrics.getTotalKgExport()));
            writer.writeLine("Diferença VL_FOB: " + NumberUtils.formatMoney(metrics.getFobDifference()));
            writer.writeLine("Diferença percentual VL_FOB: " + NumberUtils.formatPercent(metrics.getFobDifferencePercent()));
            writer.writeBlankLine();

            writer.writeSectionTitle("Insights");
            writer.writeParagraph(context.getInsightText());

            addChart(document, writer, charts.getChart1(), "Gráfico 1 - Comparativo de VL_FOB");
            addChart(document, writer, charts.getChart2(), "Gráfico 2 - Série mensal de VL_FOB");
            addChart(document, writer, charts.getChart3(), "Gráfico 3 - Top SH4 por VL_FOB");

            writer.close();
            document.save(pdfPath.toFile());
        }

        return pdfPath;
    }

    private void addChart(PDDocument document, PdfWriter writer, Path path, String title) throws IOException {
        if (path == null || !Files.exists(path)) {
            return;
        }

        writer.newPage();
        writer.writeSectionTitle(title);
        writer.writeImage(path);
    }

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "-";
        }
        return String.join(", ", values);
    }

    private int safeSize(ReportContext context) {
        return context.getFilteredRecords() == null ? 0 : context.getFilteredRecords().size();
    }

    private String valueOrDash(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String sanitizeText(String text) {
        if (text == null || text.isBlank()) {
            return "-";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "");
        return normalized
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"')
                .replace('\u00A0', ' ');
    }

    private final class PdfWriter {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream contentStream;
        private float currentY;
        private final float pageWidth;

        private PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
            this.pageWidth = page.getMediaBox().getWidth();
        }

        private void newPage() throws IOException {
            closeCurrentStream();
            this.page = new PDPage(PDRectangle.A4);
            this.document.addPage(page);
            this.contentStream = new PDPageContentStream(document, page);
            this.currentY = page.getMediaBox().getHeight() - MARGIN;
        }

        private void writeTitle(String text) throws IOException {
            writeWrappedLines(Collections.singletonList(sanitizeText(text)), titleFont, TITLE_FONT_SIZE, true);
            currentY -= 4f;
        }

        private void writeSectionTitle(String text) throws IOException {
            ensureSpace(lineHeight(SUBTITLE_FONT_SIZE) + 8f);
            writeWrappedLines(Collections.singletonList(sanitizeText(text)), subtitleFont, SUBTITLE_FONT_SIZE, true);
            currentY -= 4f;
        }

        private void writeLine(String text) throws IOException {
            writeParagraph(text);
        }

        private void writeParagraph(String text) throws IOException {
            List<String> paragraphs = splitParagraphs(text);
            for (String paragraph : paragraphs) {
                List<String> wrapped = wrapText(sanitizeText(paragraph), bodyFont, BODY_FONT_SIZE, usableWidth());
                if (wrapped.isEmpty()) {
                    writeBlankLine();
                    continue;
                }
                writeWrappedLines(wrapped, bodyFont, BODY_FONT_SIZE, false);
                currentY -= 2f;
            }
        }

        private void writeBlankLine() {
            currentY -= lineHeight(BODY_FONT_SIZE);
        }

        private void writeImage(Path imagePath) throws IOException {
            PDImageXObject image = PDImageXObject.createFromFileByContent(imagePath.toFile(), document);

            float scale = Math.min(IMAGE_MAX_WIDTH / image.getWidth(), IMAGE_MAX_HEIGHT / image.getHeight());
            scale = Math.min(scale, 1f);

            float drawWidth = image.getWidth() * scale;
            float drawHeight = image.getHeight() * scale;

            ensureSpace(drawHeight + 10f);
            contentStream.drawImage(image, MARGIN, currentY - drawHeight, drawWidth, drawHeight);
            currentY -= drawHeight + 10f;
        }

        private void writeWrappedLines(List<String> lines, PDFont font, float fontSize, boolean extraGapAfter) throws IOException {
            float heightNeeded = lineHeight(fontSize) * lines.size();
            ensureSpace(heightNeeded + (extraGapAfter ? 4f : 0f));

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(MARGIN, currentY);

            boolean first = true;
            for (String line : lines) {
                if (!first) {
                    contentStream.newLineAtOffset(0, -lineHeight(fontSize));
                }
                contentStream.showText(line);
                first = false;
            }

            contentStream.endText();
            currentY -= heightNeeded;
            if (extraGapAfter) {
                currentY -= 4f;
            }
        }

        private void ensureSpace(float requiredHeight) throws IOException {
            if (currentY - requiredHeight >= MARGIN) {
                return;
            }
            newPage();
        }

        private float usableWidth() {
            return pageWidth - (2 * MARGIN);
        }

        private float lineHeight(float fontSize) {
            return fontSize * LINE_SPACING;
        }

        private void close() throws IOException {
            closeCurrentStream();
        }

        private void closeCurrentStream() throws IOException {
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }
        }
    }

    private List<String> splitParagraphs(String text) {
        if (text == null || text.isBlank()) {
            return Collections.singletonList("-");
        }

        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] parts = normalized.split("\n\s*\n");
        List<String> paragraphs = new ArrayList<>();
        for (String part : parts) {
            String clean = part.replace('\n', ' ').trim();
            if (!clean.isEmpty()) {
                paragraphs.add(clean);
            }
        }
        return paragraphs.isEmpty() ? Collections.singletonList("-") : paragraphs;
    }

    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        if (text == null || text.isBlank()) {
            return Collections.singletonList("-");
        }

        String[] words = text.trim().split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
            float candidateWidth = font.getStringWidth(candidate) / 1000 * fontSize;

            if (candidateWidth <= maxWidth) {
                currentLine.setLength(0);
                currentLine.append(candidate);
                continue;
            }

            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
            }

            if (font.getStringWidth(word) / 1000 * fontSize <= maxWidth) {
                currentLine.append(word);
            } else {
                lines.addAll(breakLongWord(word, font, fontSize, maxWidth));
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private List<String> breakLongWord(String word, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> parts = new ArrayList<>();
        StringBuilder chunk = new StringBuilder();

        for (char c : word.toCharArray()) {
            String candidate = chunk.toString() + c;
            float candidateWidth = font.getStringWidth(candidate) / 1000 * fontSize;
            if (candidateWidth <= maxWidth) {
                chunk.append(c);
            } else {
                if (!chunk.isEmpty()) {
                    parts.add(chunk.toString());
                    chunk.setLength(0);
                }
                chunk.append(c);
            }
        }

        if (!chunk.isEmpty()) {
            parts.add(chunk.toString());
        }
        return parts;
    }
}
