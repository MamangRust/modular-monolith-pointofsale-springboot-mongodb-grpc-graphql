package com.sanedge.pointofsale.order.repository.statsbymerchant;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.order.models.OrderMonth;
import com.sanedge.pointofsale.order.models.OrderYear;
import com.sanedge.pointofsale.order.repository.OrderStatsSupport;

import lombok.RequiredArgsConstructor;

/**
 * Implementasi MongoDB statistik order bulanan/tahunan difilter per merchant.
 * Join orders → order_items dilakukan in-memory via {@link OrderStatsSupport}.
 */
@Repository
@RequiredArgsConstructor
public class OrderSoldOutByMerchantRepositoryImpl implements OrderSoldOutByMerchantRepository {

    private final OrderStatsSupport support;

    @Override
    public List<OrderMonth> findMonthlyOrdersByMerchant(Integer merchantId, Integer yearMonth) {
        LocalDate start = parseStartMonth(yearMonth);
        List<OrderStatsSupport.JoinedRow> rows = support.findJoinedRows(
                start.atStartOfDay(), start.plusYears(1).atStartOfDay(), merchantId);

        Map<Integer, List<OrderStatsSupport.JoinedRow>> byMonth = new HashMap<>();
        for (OrderStatsSupport.JoinedRow row : rows) {
            if (row.order().createdAt() == null) {
                continue;
            }
            byMonth.computeIfAbsent(row.order().createdAt().getMonthValue(), m -> new ArrayList<>()).add(row);
        }

        record Entry(int monthNum, OrderMonth dto) {
        }

        return byMonth.entrySet().stream()
                .map(e -> {
                    int monthNum = e.getKey();
                    Map<Integer, OrderStatsSupport.OrderRow> distinctOrders = distinctOrders(e.getValue());
                    long totalRevenue = distinctOrders.values().stream()
                            .mapToLong(OrderStatsSupport.OrderRow::totalPrice).sum();
                    int totalItemsSold = e.getValue().stream().mapToInt(OrderStatsSupport.JoinedRow::quantity).sum();
                    String monthName = Month.of(monthNum).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    return new Entry(monthNum, new OrderMonth(monthName, distinctOrders.size(),
                            totalRevenue, totalItemsSold));
                })
                .sorted(Comparator.comparingInt(Entry::monthNum))
                .map(Entry::dto)
                .toList();
    }

    @Override
    public List<OrderYear> findYearlyOrdersByMerchant(Integer merchantId, Integer yearMonth) {
        int year = parseYear(yearMonth);
        List<OrderStatsSupport.JoinedRow> rows = support.findJoinedRows(
                LocalDate.of(year - 4, 1, 1).atStartOfDay(),
                LocalDate.of(year + 1, 1, 1).atStartOfDay(), merchantId);

        Map<Integer, List<OrderStatsSupport.JoinedRow>> byYear = new HashMap<>();
        for (OrderStatsSupport.JoinedRow row : rows) {
            if (row.order().createdAt() == null) {
                continue;
            }
            byYear.computeIfAbsent(row.order().createdAt().getYear(), y -> new ArrayList<>()).add(row);
        }

        record Entry(int yearNum, OrderYear dto) {
        }

        return byYear.entrySet().stream()
                .map(e -> {
                    int yearNum = e.getKey();
                    Map<Integer, OrderStatsSupport.OrderRow> distinctOrders = distinctOrders(e.getValue());
                    long totalRevenue = distinctOrders.values().stream()
                            .mapToLong(OrderStatsSupport.OrderRow::totalPrice).sum();
                    int totalItemsSold = e.getValue().stream().mapToInt(OrderStatsSupport.JoinedRow::quantity).sum();
                    Set<Integer> activeCashiers = new HashSet<>();
                    Set<Integer> uniqueProducts = new HashSet<>();
                    for (OrderStatsSupport.JoinedRow row : e.getValue()) {
                        if (row.order().cashierId() != null) {
                            activeCashiers.add(row.order().cashierId());
                        }
                        uniqueProducts.add(row.productId());
                    }
                    return new Entry(yearNum, new OrderYear(String.valueOf(yearNum), distinctOrders.size(),
                            totalRevenue, totalItemsSold, activeCashiers.size(), uniqueProducts.size()));
                })
                .sorted(Comparator.comparingInt(Entry::yearNum))
                .map(Entry::dto)
                .toList();
    }

    private static Map<Integer, OrderStatsSupport.OrderRow> distinctOrders(List<OrderStatsSupport.JoinedRow> rows) {
        Map<Integer, OrderStatsSupport.OrderRow> orders = new LinkedHashMap<>();
        for (OrderStatsSupport.JoinedRow row : rows) {
            orders.putIfAbsent(row.order().orderId(), row.order());
        }
        return orders;
    }

    private static LocalDate parseStartMonth(Integer yearMonth) {
        String value = String.valueOf(yearMonth);
        if (value.length() > 4) {
            return LocalDate.of(Integer.parseInt(value.substring(0, 4)),
                    Integer.parseInt(value.substring(4, 6)), 1);
        }
        return LocalDate.of(yearMonth, 1, 1);
    }

    private static int parseYear(Integer yearMonth) {
        return Integer.parseInt(String.valueOf(yearMonth).substring(0, 4));
    }
}
