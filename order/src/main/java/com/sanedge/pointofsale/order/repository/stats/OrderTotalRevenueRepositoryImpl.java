package com.sanedge.pointofsale.order.repository.stats;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.order.models.OrderYearTotalRevenue;
import com.sanedge.pointofsale.order.repository.OrderStatsSupport;

import lombok.RequiredArgsConstructor;

/**
 * Implementasi MongoDB: agregasi 1 koleksi `orders` (match deleted_at null +
 * rentang created_at, lalu sum total_price) dilakukan lewat
 * {@link OrderStatsSupport#findOrders} dan pengelompokan di sisi Java.
 */
@Repository
@RequiredArgsConstructor
public class OrderTotalRevenueRepositoryImpl implements OrderTotalRevenueRepository {

    private final OrderStatsSupport support;

    private record Entry(int year, int monthNum, OrderMonthTotalRevenue dto) {
    }

    @Override
    public List<OrderMonthTotalRevenue> findMonthlyTotalRevenue(
            Integer year1, Integer month1, Integer year2, Integer month2) {

        List<Entry> entries = new ArrayList<>();
        entries.add(monthlyEntry(year1, month1));
        entries.add(monthlyEntry(year2, month2));

        return entries.stream()
                .sorted(Comparator.comparingInt(Entry::year).reversed()
                        .thenComparing(Comparator.comparingInt(Entry::monthNum).reversed()))
                .map(Entry::dto)
                .toList();
    }

    @Override
    public List<OrderYearTotalRevenue> findYearlyTotalRevenue(Integer year) {
        List<OrderYearTotalRevenue> result = new ArrayList<>();
        result.add(new OrderYearTotalRevenue(String.valueOf(year), yearlyRevenue(year)));
        result.add(new OrderYearTotalRevenue(String.valueOf(year - 1), yearlyRevenue(year - 1)));
        result.sort(Comparator.comparingInt(
                (OrderYearTotalRevenue r) -> Integer.parseInt(r.getYear())).reversed());
        return result;
    }

    private Entry monthlyEntry(Integer year, Integer month) {
        if (year == null || month == null || month < 1 || month > 12) {
            return new Entry(year == null ? 0 : year, month == null ? 0 : month,
                    new OrderMonthTotalRevenue(String.valueOf(year), "", 0));
        }
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int revenue = (int) support
                .findOrders(firstDay.atStartOfDay(), firstDay.plusMonths(1).atStartOfDay(), null)
                .stream()
                .mapToLong(OrderStatsSupport.OrderRow::totalPrice)
                .sum();

        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return new Entry(year, month, new OrderMonthTotalRevenue(String.valueOf(year), monthName, revenue));
    }

    private int yearlyRevenue(Integer year) {
        if (year == null) {
            return 0;
        }
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        return (int) support.findOrders(firstDay.atStartOfDay(), firstDay.plusYears(1).atStartOfDay(), null)
                .stream()
                .mapToLong(OrderStatsSupport.OrderRow::totalPrice)
                .sum();
    }
}
