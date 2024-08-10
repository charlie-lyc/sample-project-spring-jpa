package com.ems.mockup.service;

import com.ems.mockup.domain.SampleDatum;
import com.ems.mockup.repository.SampleDatumRepository;

import com.opencsv.CSVParser;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class SampleDatumService {

    private final SampleDatumRepository sampleDatumRepository;
    private final Logger logger;
    @Autowired
    public SampleDatumService(SampleDatumRepository sampleDatumRepository, Logger logger) {
        this.sampleDatumRepository = sampleDatumRepository;
        this.logger = logger;
    }

    public List<SampleDatum> getAllSampleData() {
        return sampleDatumRepository.findAll();
    }

    public Optional<SampleDatum> getSampleDatumByHour(int hour) {
        return Optional.ofNullable(sampleDatumRepository.findSampleDatumByHour(hour));
    }

    public File exportSampleDataToCSV(String fileName) throws IOException {
        List<SampleDatum> sampleData = getAllSampleData();
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(fileWriter);
        try {
            String[] header = { "hour", "pvUsage", "essUsage", "equipLight", "equipSmart", "equipAqua", "equipAir",
                    "chargeMinutes", "chargeVolt", "chargeCurrent", "dischargeMinutes", "dischargeVolt", "dischargeCurrent",
                    "solarSupplyRate", "electricDemandRate", "batterySupplyRate", "batteryOpRate", "solarPower", "solarOpRate",
                    "solarDailyGen", "electricDemand", "electricConsume", "batteryDayCharge", "batteryDayDischarge", "batteryDayChargeHours",
                    "batteryDayDischargeHours", "batteryAccCharge", "batteryAccChargeHours", "batteryAccDischarge",
                    "batteryAccDischargeHours", "essCharge", "createdAt", "updatedAt" };
            csvWriter.writeNext(header);
            for (SampleDatum sampleDatum: sampleData) {
                csvWriter.writeNext(new String[] {
                        String.valueOf(sampleDatum.getHour()),
                        String.valueOf(sampleDatum.getPvUsage()),
                        String.valueOf(sampleDatum.getEssUsage()),
                        String.valueOf(sampleDatum.getEquipLight()),
                        String.valueOf(sampleDatum.getEquipSmart()),
                        String.valueOf(sampleDatum.getEquipAqua()),
                        String.valueOf(sampleDatum.getEquipAir()),
                        String.valueOf(sampleDatum.getChargeMinutes()),
                        String.valueOf(sampleDatum.getChargeVolt()),
                        String.valueOf(sampleDatum.getChargeCurrent()),
                        String.valueOf(sampleDatum.getDischargeMinutes()),
                        String.valueOf(sampleDatum.getDischargeVolt()),
                        String.valueOf(sampleDatum.getDischargeCurrent()),
                        String.valueOf(sampleDatum.getSolarSupplyRate()),
                        String.valueOf(sampleDatum.getElectricDemandRate()),
                        String.valueOf(sampleDatum.getBatterySupplyRate()),
                        String.valueOf(sampleDatum.getBatteryOpRate()),
                        String.valueOf(sampleDatum.getSolarPower()),
                        String.valueOf(sampleDatum.getSolarOpRate()),
                        String.valueOf(sampleDatum.getSolarDailyGen()),
                        String.valueOf(sampleDatum.getElectricDemand()),
                        String.valueOf(sampleDatum.getElectricConsume()),
                        String.valueOf(sampleDatum.getBatteryDayCharge()),
                        String.valueOf(sampleDatum.getBatteryDayDischarge()),
                        String.valueOf(sampleDatum.getBatteryDayChargeHours()),
                        String.valueOf(sampleDatum.getBatteryDayDischargeHours()),
                        String.valueOf(sampleDatum.getBatteryAccCharge()),
                        String.valueOf(sampleDatum.getBatteryAccChargeHours()),
                        String.valueOf(sampleDatum.getBatteryAccDischarge()),
                        String.valueOf(sampleDatum.getBatteryAccDischargeHours()),
                        String.valueOf(sampleDatum.getEssCharge()),
                        sampleDatum.getCreatedAt().toString(),
                        sampleDatum.getUpdatedAt().toString(),
                });
            }
        } finally {
            csvWriter.close();
        }
        return file;
    }

    public void importSampleDataFromCSV(MultipartFile file) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            String headerLine = bufferedReader.readLine();
            List<SampleDatum> sampleData = bufferedReader.lines().map((line) -> {
                String[] fields = line.split(",");
                if (fields.length != 31) {
                    logger.error("Does not match the expected number of columns: " + fields.length);
                    throw new IllegalArgumentException("Does not match the expected number of columns: " + fields.length);
                }
                SampleDatum sampleDatum = new SampleDatum();
                try {
                    sampleDatum.setHour(Integer.parseInt(fields[0]));
                    sampleDatum.setPvUsage(Double.parseDouble(fields[1]));
                    sampleDatum.setEssUsage(Double.parseDouble(fields[2]));
                    sampleDatum.setEquipLight(Double.parseDouble(fields[3]));
                    sampleDatum.setEquipSmart(Double.parseDouble(fields[4]));
                    sampleDatum.setEquipAqua(Double.parseDouble(fields[5]));
                    sampleDatum.setEquipAir(Double.parseDouble(fields[6]));
                    sampleDatum.setChargeMinutes(Double.parseDouble(fields[7]));
                    sampleDatum.setChargeVolt(Double.parseDouble(fields[8]));
                    sampleDatum.setChargeCurrent(Double.parseDouble(fields[9]));
                    sampleDatum.setDischargeMinutes(Double.parseDouble(fields[10]));
                    sampleDatum.setDischargeVolt(Double.parseDouble(fields[11]));
                    sampleDatum.setDischargeCurrent(Double.parseDouble(fields[12]));
                    sampleDatum.setSolarSupplyRate(Double.parseDouble(fields[13]));
                    sampleDatum.setElectricDemandRate(Double.parseDouble(fields[14]));
                    sampleDatum.setBatterySupplyRate(Double.parseDouble(fields[15]));
                    sampleDatum.setBatteryOpRate(Double.parseDouble(fields[16]));
                    sampleDatum.setSolarPower(Double.parseDouble(fields[17]));
                    sampleDatum.setSolarOpRate(Double.parseDouble(fields[18]));
                    sampleDatum.setSolarDailyGen(Double.parseDouble(fields[19]));
                    sampleDatum.setElectricDemand(Double.parseDouble(fields[20]));
                    sampleDatum.setElectricConsume(Double.parseDouble(fields[21]));
                    sampleDatum.setBatteryDayCharge(Double.parseDouble(fields[22]));
                    sampleDatum.setBatteryDayDischarge(Double.parseDouble(fields[23]));
                    sampleDatum.setBatteryDayChargeHours(Double.parseDouble(fields[24]));
                    sampleDatum.setBatteryDayDischargeHours(Double.parseDouble(fields[25]));
                    sampleDatum.setBatteryAccCharge(Double.parseDouble(fields[26]));
                    sampleDatum.setBatteryAccChargeHours(Double.parseDouble(fields[27]));
                    sampleDatum.setBatteryAccDischarge(Double.parseDouble(fields[28]));
                    sampleDatum.setBatteryAccDischargeHours(Double.parseDouble(fields[29]));
                    sampleDatum.setEssCharge(Double.parseDouble(fields[30]));
                } catch (NumberFormatException e) {
                    logger.error("Invalid format of number in rows: " + line);
                    throw new IllegalArgumentException("Invalid format of number in rows: " + line);
                }
                return sampleDatum;
            }).collect(Collectors.toList());
            // Clear existing data and save new data
            sampleDatumRepository.deleteAll();
            sampleDatumRepository.saveAll(sampleData);
        } finally {
            //...
        }
    }

}
