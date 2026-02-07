package com.cs.sp.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author sb
 * @date 2023/6/2 11:44
 */
@Slf4j
public class DateUtil {

    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final String YMD = "yyyyMMdd";


    public static Integer getYmdOfNday(int n, Integer ymd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YMD);
        LocalDate localDate = LocalDate.parse(ymd.toString(), formatter);
        localDate = localDate.plusDays(n);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmdOfNday(int n, Date date) {
        LocalDate localDate = date.toInstant().atZone(ZONE_ID).toLocalDate();
        localDate = localDate.plusDays(n);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmdOfNday(int n) {
        LocalDate localDate = LocalDate.now(ZONE_ID).plusDays(n);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmdOfNdayBefore(int n) {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(n);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmdOfDayBeforeYesterday() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(2);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmdOfYesterday() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(1);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmd() {
        LocalDate localDate = LocalDate.now(ZONE_ID);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getShortYmd() {
        LocalDate localDate = LocalDate.now(ZONE_ID);
        return localDate.getYear() % 100 * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }
    public static Integer getShortYmdOfNday(int n) {
        LocalDate localDate = LocalDate.now(ZONE_ID).plusDays(n);
        return localDate.getYear() % 100 * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Long getNHourFull(int n){
        LocalDateTime localDate = LocalDateTime.now(ZONE_ID);
        if(n != 0){
            localDate = localDate.plusHours(n);
        }
        return localDate.getYear() * 1000000L + localDate.getMonthValue() * 10000L + localDate.getDayOfMonth() * 100L
                + localDate.getHour();
    }

    public static Long getYmdAndHourFull(){
        LocalDateTime localDate = LocalDateTime.now(ZONE_ID);
        return localDate.getYear() * 1000000L + localDate.getMonthValue() * 10000L + localDate.getDayOfMonth() * 100L
                + localDate.getHour();
    }

    public static Long getYmdAndHourFullOfTimestamp(Long timestamp){
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
        return localDate.getYear() * 1000000L + localDate.getMonthValue() * 10000L + localDate.getDayOfMonth() * 100L
                + localDate.getHour();
    }

    public static Long getNHourMinuteFull(int n){
        LocalDateTime localDate = LocalDateTime.now(ZONE_ID);
        if(n != 0){
            localDate = localDate.plusMinutes(n);
        }
        return localDate.getYear() * 100000000L + localDate.getMonthValue() * 1000000L + localDate.getDayOfMonth() * 10000L
                + localDate.getHour() * 100L + localDate.getMinute();
    }

    public static Long getYmdAndHourMinuteFullOfTimestamp(Long timestamp){
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
        return localDate.getYear() * 100000000L + localDate.getMonthValue() * 1000000L + localDate.getDayOfMonth() * 10000L
                + localDate.getHour() * 100L + localDate.getMinute();
    }


    public static Long getYmdAndHourMinuteFull(){
        LocalDateTime localDate = LocalDateTime.now(ZONE_ID);
        return localDate.getYear() * 100000000L + localDate.getMonthValue() * 1000000L + localDate.getDayOfMonth() * 10000L
                + localDate.getHour() * 100L + localDate.getMinute();
    }
    public static Integer[] getYmdAndHourMinute() {
        LocalDateTime localDate = LocalDateTime.now(ZONE_ID);
        return new Integer[]{
                localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth(),
                localDate.getHour() * 100 + localDate.getMinute()
        };
    }

    public static Integer getYm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) * 100 + (calendar.get(Calendar.MONTH) + 1);
    }

    public static Integer getHourMinute() {
        LocalTime now = LocalTime.now(ZONE_ID);
        return now.getHour() * 100 + now.getMinute();
    }

    public static Integer getHourMinute(Long timestamp){
        LocalTime localTime = Instant.ofEpochMilli(timestamp).atZone(ZONE_ID).toLocalTime();
        return localTime.getHour() * 100 + localTime.getMinute();
    }

    public static Integer getHourMinute(Date date){
        LocalTime localTime = date.toInstant().atZone(ZONE_ID).toLocalTime();
        return localTime.getHour() * 100 + localTime.getMinute();
    }

    public static Integer getHourMinuteOfNMin(Long time, Integer n) {
        return getHourMinuteOfNMin(new Date(time), n);
    }

    public static Integer getHourMinuteOfNMin(Date date, Integer n) {
        LocalTime localTime = date.toInstant().atZone(ZONE_ID).toLocalDateTime().toLocalTime().plusMinutes(n);
        return localTime.getHour() * 100 + localTime.getMinute();
    }


    public static Integer getHourMinuteOfNMin(Integer n) {
        LocalTime now = LocalTime.now(ZONE_ID).plusMinutes(n);
        return now.getHour() * 100 + now.getMinute();
    }

    public static Integer getYmd(Long timestamp) {
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }

    public static Integer getYmd(Date date) {
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZONE_ID);
        return localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
    }


    public static LocalDate format(String ymd) {
        return format(ymd, "yyyyMMdd");
    }

    public static LocalDate format(String ymd, String foramt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(foramt);
        return LocalDate.parse(ymd, formatter);
    }

    public static long getTimestampOf(String time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    public static long getTimeOf(long sourceTimestamp, int targetDate) {
        LocalTime localTime = Instant.ofEpochMilli(sourceTimestamp).atZone(ZONE_ID).toLocalTime();
        LocalDate date = LocalDate.parse(String.valueOf(targetDate), DateTimeFormatter.ofPattern(YMD));
        return LocalDateTime.of(date, localTime).atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 根据指定yyyyMMdd格式日期，+ 加减的天数，获取新的yyyyMMdd的日期
     * @param ymd
     * @param days
     * @return
     */
    public static int addDay(Integer ymd, int days) {
        LocalDate date = LocalDate.parse(String.valueOf(ymd), DateTimeFormatter.ofPattern(YMD));
        LocalDate newDate = date.plusDays(days);
        return Integer.parseInt(newDate.format(DateTimeFormatter.ofPattern(YMD)));
    }

    public static String formatTime(Long coldDownAt) {
// 将时间戳转为LocalDateTime（默认使用系统时区）
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(coldDownAt),
                ZONE_ID
        );

        // 定义目标格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化输出
        return dateTime.format(formatter);
    }

    public static Long startOf(Integer ymd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YMD);
        LocalDate date = LocalDate.parse(ymd.toString(), formatter);
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZONE_ID);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static Long startOf(Long timestamp) {
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
        return localDate.toLocalDate().atStartOfDay()
                .atZone(ZONE_ID)
                .toInstant().toEpochMilli();
    }
}
