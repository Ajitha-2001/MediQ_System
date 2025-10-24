package hms.service.analytics;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ChartService {

    public String createLineChartAsBase64(String title, List<? extends Number> yData) {
        try {
            List<Integer> xData = IntStream.range(1, yData.size() + 1).boxed().collect(Collectors.toList());

            XYChart chart = new XYChartBuilder().width(800).height(400).title(title).xAxisTitle("Day of Month").build();
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            chart.getStyler().setLegendVisible(false);
            chart.addSeries("data", xData, yData);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, baos, BitmapEncoder.BitmapFormat.PNG);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            return "";
        }
    }
}