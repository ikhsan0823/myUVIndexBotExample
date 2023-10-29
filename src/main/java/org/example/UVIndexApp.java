package org.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;

public class UVIndexApp {
    public static void main(String[] args) {
        // Panggil kode Anda untuk mengambil data UV Index
        String uvIndexData = getDataFromUVIndexAPI();

        // Cetak data UV Index
        System.out.println("Data UV Index:");
        System.out.println(uvIndexData);
    }

    public static String getDataFromUVIndexAPI() {
        String apiUrl = "https://currentuvindex.com/api/v1/uvi?latitude=-5.142096641679418&longitude=119.50668172968246";
        String uvIndexData = "";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);

            // Lakukan permintaan HTTP GET
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // Periksa kode status respons
                int statusCode = response.getStatusLine().getStatusCode(); // Perbaikan
                if (statusCode == 200) {
                    // Baca respons sebagai string
                    String responseBody = EntityUtils.toString(response.getEntity());

                    // Di sini Anda dapat memproses respons JSON sesuai dengan kebutuhan Anda.
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.getBoolean("ok")) {
                        // Respons sukses
                        double latitude = jsonResponse.getDouble("latitude");
                        double longitude = jsonResponse.getDouble("longitude");
                        JSONObject now = jsonResponse.getJSONObject("now");
                        String nowTime = now.getString("time");
                        double nowUvi = now.getDouble("uvi");

                        // Pisahkan waktu dan jam dengan spasi
                        String formattedNowTime = nowTime;
                        if (nowTime.contains(" ")) {
                            String[] timeParts = nowTime.split(" ");
                            if (timeParts.length >1) {
                                formattedNowTime = timeParts[0] + " " + timeParts[1];
                            }
                        }

                        StringBuilder dataBuilder = new StringBuilder();
                        dataBuilder.append("Latitude: ").append(latitude).append("\n");
                        dataBuilder.append("Longitude: ").append(longitude).append("\n");
                        dataBuilder.append("Now Time: ").append(formattedNowTime).append("\n");
                        dataBuilder.append("Now UV: ").append(nowUvi).append("\n");

                        JSONArray forecastArray = jsonResponse.getJSONArray("forecast");
                        JSONArray filteredForecastArray = new JSONArray();

                        // Mendapatkan waktu saat ini
                        Instant currentTime = Instant.now();

                        for (int i = 0; i < forecastArray.length(); i++) {
                            JSONObject forecast = forecastArray.getJSONObject(i);
                            String forecastTime = forecast.getString("time");

                            // Mengubah string waktu menjadi objek waktu
                            Instant forecastInstant = Instant.parse(forecastTime);

                            // Menghitung selisih waktu antara waktu saat ini dan waktu prediksi
                            long hoursDifference = currentTime.until(forecastInstant, java.time.temporal.ChronoUnit.HOURS);

                            // Jika selisih waktu dalam 6 jam ke depan, tambahkan ke filteredForecastArray
                            if (hoursDifference >= 0 && hoursDifference <= 6) {
                                filteredForecastArray.put(forecast);
                            }
                        }

                        for (int i = 0; i < filteredForecastArray.length(); i++) {
                            JSONObject forecast = filteredForecastArray.getJSONObject(i);
                            String forecastTime = forecast.getString("time");
                            double forecastUvi = forecast.getDouble("uvi");
                            dataBuilder.append("Forecast Time: ").append(forecastTime).append("\n");
                            dataBuilder.append("Forecast UV: ").append(forecastUvi).append("\n");
                        }

                        uvIndexData = dataBuilder.toString();
                    } else {
                        // Respons gagal
                        String errorMessage = jsonResponse.getString("message");
                        System.err.println("Gagal: " + errorMessage);
                    }
                } else {
                    System.err.println("Gagal mengakses API. Kode status: " + statusCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uvIndexData;
    }
}
