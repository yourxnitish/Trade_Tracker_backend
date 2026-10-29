package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.dto.TradeRequest;
import com.tradetrack.tradingjournal.entity.Trade;
import com.tradetrack.tradingjournal.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public ResponseEntity<Page<Trade>> getTrades(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String outcome,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String exchange,
            @RequestParam(required = false) String strategy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tradeDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction dir = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Page<Trade> result = tradeService.searchAndFilterTrades(
                search, startDate, endDate, status, outcome, direction, instrument, exchange, strategy, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Trade>> getAllTrades() {
        return ResponseEntity.ok(tradeService.getAllTrades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trade> getTradeById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeService.getTradeById(id));
    }

    @PostMapping
    public ResponseEntity<Trade> createTrade(@Valid @RequestBody TradeRequest request) {
        Trade trade = tradeService.createTrade(request);
        return new ResponseEntity<>(trade, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trade> updateTrade(@PathVariable Long id, @Valid @RequestBody TradeRequest request) {
        Trade trade = tradeService.updateTrade(id, request);
        return ResponseEntity.ok(trade);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instruments")
    public ResponseEntity<List<String>> getInstruments() {
        return ResponseEntity.ok(tradeService.getDistinctInstruments());
    }

    @GetMapping("/strategies")
    public ResponseEntity<List<String>> getStrategies() {
        return ResponseEntity.ok(tradeService.getDistinctStrategies());
    }

    @GetMapping("/exchanges")
    public ResponseEntity<List<String>> getExchanges() {
        return ResponseEntity.ok(tradeService.getDistinctExchanges());
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportTradesToCsv() {
        List<Trade> trades = tradeService.getAllTrades();
        StringBuilder csv = new StringBuilder();
        csv.append("Trade ID,Date,Entry Time,Exit Time,Instrument,Symbol,Exchange,Segment,Direction,Status,Entry Price,Exit Price,Quantity,Target,Stop Loss,Brokerage,Other Charges,Gross PnL,Net PnL,Risk Amount,Reward Amount,R:R Ratio,Strategy,Mistakes,Lessons Learned\n");

        for (Trade t : trades) {
            csv.append(escapeCsv(t.getId())).append(",")
               .append(escapeCsv(t.getTradeDate())).append(",")
               .append(escapeCsv(t.getEntryTime())).append(",")
               .append(escapeCsv(t.getExitTime())).append(",")
               .append(escapeCsv(t.getInstrumentName())).append(",")
               .append(escapeCsv(t.getSymbol())).append(",")
               .append(escapeCsv(t.getExchange())).append(",")
               .append(escapeCsv(t.getSegment())).append(",")
               .append(escapeCsv(t.getDirection())).append(",")
               .append(escapeCsv(t.getStatus())).append(",")
               .append(escapeCsv(t.getEntryPrice())).append(",")
               .append(escapeCsv(t.getExitPrice())).append(",")
               .append(escapeCsv(t.getQuantity())).append(",")
               .append(escapeCsv(t.getTargetPrice())).append(",")
               .append(escapeCsv(t.getStopLoss())).append(",")
               .append(escapeCsv(t.getBrokerage())).append(",")
               .append(escapeCsv(t.getOtherCharges())).append(",")
               .append(escapeCsv(t.getGrossPnl())).append(",")
               .append(escapeCsv(t.getNetPnl())).append(",")
               .append(escapeCsv(t.getRiskAmount())).append(",")
               .append(escapeCsv(t.getRewardAmount())).append(",")
               .append(escapeCsv(t.getRiskRewardRatio())).append(",")
               .append(escapeCsv(t.getStrategy())).append(",")
               .append(escapeCsv(t.getMistakes())).append(",")
               .append(escapeCsv(t.getLessonsLearned())).append("\n");
        }

        byte[] bytes = csv.toString().getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "trades_" + LocalDate.now() + ".csv");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private String escapeCsv(Object val) {
        if (val == null) return "";
        String str = val.toString().replace("\"", "\"\"");
        if (str.contains(",") || str.contains("\n") || str.contains("\"")) {
            return "\"" + str + "\"";
        }
        return str;
    }
}
