package com.partior.client.util;

import com.google.gson.Gson;
import com.partior.client.dto.*;
import com.partior.client.dto.enums.Currency;
import com.partior.client.dto.enums.ProposalStatus;
import com.partior.client.dto.enums.TransactionType;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JsonClientWrapper {


    @Value("${dapps.service.host:localhost}")
    private String host;

    @Value("${dapps.service.port:8080}")
    private String port;

    @Value("${dapps.jsonapi.http-timeout-ms:5000}")
    private int httpTimeout = 5000;

    private WebClient client;

    private Gson gson;

    public JsonClientWrapper(@Value("${dapps.service.host}")String host, @Value("${dapps.service.port:8081}")String port ) {
        this.host = host;
        this.port = port;
        initializeClient();
    }

//    public JsonClientWrapper() {
//        initializeClient();
//    }
    private void initializeClient() {
        if (client == null) {
            var url = String.format("http://%s:%s", host, port);
            log.info("dapps url:{}", url);

            HttpClient httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpTimeout)
                    .responseTimeout(Duration.ofMillis(httpTimeout))
                    .doOnConnected(conn ->
                            conn.addHandlerLast(new ReadTimeoutHandler(httpTimeout, TimeUnit.MILLISECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(httpTimeout, TimeUnit.MILLISECONDS)));


            client = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        }
    }

    public  <T> T  responseStringToObject(String jsonResponse, Class<T> t){
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, t);
    }

    public  String objectToJson(Object scr){
        Gson gson = new Gson();
        return gson.toJson(scr);
    }

    public List<CentralBankDto> listCentralBanks() throws Exception {
        List<CentralBankDto> centralBankDtoList = new ArrayList<>();
        centralBankDtoList.add( new CentralBankDto("MAS" , "SGD") );
        centralBankDtoList.add( new CentralBankDto("BI" , "IDR") );
        return centralBankDtoList;
    }


    public List<AccountOwnerResponseDto> listParticipantBanks(String centralBankUesr) throws Exception {
        List<AccountOwnerResponseDto> accountOwnerResponseDtos = null;
        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAllAccountOwners/" + centralBankUesr)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

       JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            accountOwnerResponseDtos = response.result.stream().map(
                    result -> {
                        Map payload = (Map)result.payload;
                        AccountOwnerResponseDto accountOwnerResponseDto = new AccountOwnerResponseDto();
                        try {
                            org.apache.commons.beanutils.BeanUtils.populate(accountOwnerResponseDto, (Map)result.payload);

                            accountOwnerResponseDto.setSponsorParty(  (String) payload.get("sponsor") );
                            accountOwnerResponseDto.setCentralBankParty(   (String) payload.get("centralBank")  );
                            accountOwnerResponseDto.setLocal( (Boolean) payload.get("isLocal") );

//                            accountOwnerResponseDto.setSponsorParty(  (String) ((Map)result.payload).get("sponsor") );
//                            accountOwnerResponseDto.setCentralBankParty(  (String) ((Map)result.payload).get("centralBank")  );
//                            accountOwnerResponseDto.setLocal( Boolean.valueOf(  ((Map)result.payload).get("isLocal").toString()).booleanValue());


                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return accountOwnerResponseDto ;
                    }
            ).collect(Collectors.toList());
        }

        return accountOwnerResponseDtos;
    }

    public List<CSDAccountOwner> listCsdParticipantBanks(String centralBankUesr) throws Exception {
        List<CSDAccountOwner> accountOwnerResponseDtos = null;
        Flux<CSDAccountOwner> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAllCsdAccountOwners/" + centralBankUesr)
                .retrieve()
                .bodyToFlux(CSDAccountOwner.class);

        return accountOwnerFlux.collectList().block();
    }

    public List<CSDAccount> listCsdParticipantBankAccountId(String bankShortName) throws Exception {

        Flux<CSDAccount> accountOwnerFlux = client
                .get()
                .uri("/api/search/getCsdAccounts/" + bankShortName)
                .retrieve()
                .bodyToFlux(CSDAccount.class);

        return accountOwnerFlux.collectList().block();
    }

    public List<AccountOwnerResponseDto> listDisabledParticipantBanks(String centralBankUesr) throws Exception {
        List<AccountOwnerResponseDto> accountOwnerResponseDtos = null;
        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search//getDisabledBanks/" + centralBankUesr)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);
        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            accountOwnerResponseDtos = response.result.stream().map(
                    result -> {
                        Map payload = (Map)result.payload;

                        AccountOwnerResponseDto accountOwnerResponseDto = new AccountOwnerResponseDto();
                        try {
                            org.apache.commons.beanutils.BeanUtils.populate(accountOwnerResponseDto, (Map)payload.get("accountOwner"));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return accountOwnerResponseDto ;
                    }
            ).collect(Collectors.toList());
        }

        return accountOwnerResponseDtos;
    }

    public List<AccountOwnerResponseDto> listParticipantAccounts(String centralBankUesr) throws Exception {
        List<AccountOwnerResponseDto> accountOwnerResponseDtos = null;
        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAllAccounts/" + centralBankUesr)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            accountOwnerResponseDtos = response.result.stream().map(
                    result -> {
                        
                        AccountOwnerResponseDto accountOwnerResponseDto = new AccountOwnerResponseDto();
                        try {
                            org.apache.commons.beanutils.BeanUtils.populate(accountOwnerResponseDto, (Map)result.payload);
                            accountOwnerResponseDto.setSponsorParty(  (String) ((Map)result.payload).get("sponsor") );
                            accountOwnerResponseDto.setCentralBankParty(  (String) ((Map)result.payload).get("centralBank")  );
                            accountOwnerResponseDto.setLocal( Boolean.valueOf(  ((Map)result.payload).get("isLocal").toString()).booleanValue()  );

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return accountOwnerResponseDto ;
                    }
            ).collect(Collectors.toList());
        }

        return accountOwnerResponseDtos;
    }

    /**
     * get all account of the participant bank
     * @param bic
     * @return
     * @throws Exception
     */
    public List<AccountOwnerResponseDto> listParticipantBankAccounts(String bic) throws Exception {
        List<AccountOwnerResponseDto> accountOwnerResponseDtos = null;
        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAccounts/" + bic)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            accountOwnerResponseDtos = response.result.stream().map(
                    result -> {

                        AccountOwnerResponseDto accountOwnerResponseDto = new AccountOwnerResponseDto();
                        Map payload = (Map)result.payload;
                        accountOwnerResponseDto.setAccountId(  String.valueOf(payload.get("accountId")) );
                        try {
                            org.apache.commons.beanutils.BeanUtils.populate(accountOwnerResponseDto,  (Map)payload.get("accountOwner"));
                            accountOwnerResponseDto.setSponsorParty(  (String) ((Map)payload.get("accountOwner")).get("sponsor") );
                            accountOwnerResponseDto.setCentralBankParty(   (String) ((Map)payload.get("accountOwner")).get("centralBank")  );
                            accountOwnerResponseDto.setLocal( (Boolean) ((Map)payload.get("accountOwner")).get("isLocal") );

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return accountOwnerResponseDto ;
                    }
            ).collect(Collectors.toList());
        }
        return accountOwnerResponseDtos;
    }

    /**
     *
     * @param centralBank
     * @return
     * @throws Exception
     */
    public List<TransactionReceiptDto> listCentralBankTransactions(String centralBank) throws Exception {

        List<TransactionReceiptDto> transactionReceiptDtos = new ArrayList<>();

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAllTransactions/" + centralBank)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200) {
            transactionReceiptDtos = response.result.stream().map(
                    result -> {
                        TransactionReceiptDto transactionReceiptDto = new TransactionReceiptDto();
                        Map payload = (Map)result.payload;
                        try {
                            transactionReceiptDto.setTimestamp(  String.valueOf(payload.get("timestamp")) );
                            transactionReceiptDto.setToParty(String.valueOf(payload.get("toParty")));
                            transactionReceiptDto.setToAccountId( String.valueOf(payload.get("toAccountId")));
                            transactionReceiptDto.setTxnType(String.valueOf(payload.get("txnType")));
                            transactionReceiptDto.setStatusReason(String.valueOf(payload.get("statusReason")));

                            org.apache.commons.beanutils.BeanUtils.populate(transactionReceiptDto,  payload);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        return transactionReceiptDto ;
                    }
            ).collect(Collectors.toList());
        }

        return transactionReceiptDtos;
    }

    public List<TransactionReceiptDto> listParticipantBankTransactions(String bic) throws Exception {

        List<TransactionReceiptDto> transactionReceiptDtos =  new ArrayList<>();

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getTransactions/" + bic)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200) {
            transactionReceiptDtos = response.result.stream().map(
                    result -> {
                        TransactionReceiptDto transactionReceiptDto = new TransactionReceiptDto();
                        Map payload = (Map)result.payload;
                        try {
                            transactionReceiptDto.setTimestamp(  String.valueOf(payload.get("timestamp")) );
                            transactionReceiptDto.setToParty(String.valueOf(payload.get("toParty")));
                            transactionReceiptDto.setToAccountId( String.valueOf(payload.get("toAccountId")));
                            transactionReceiptDto.setTxnType(String.valueOf(payload.get("txnType")));
                            transactionReceiptDto.setStatusReason(String.valueOf(payload.get("statusReason")));


                            org.apache.commons.beanutils.BeanUtils.populate(transactionReceiptDto,  payload);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        return transactionReceiptDto ;
                    }
            ).collect(Collectors.toList());
        }

        return transactionReceiptDtos;
    }


    public List<JsonResponseDto> listParticipantBankAccountTransactions(String bic, String accountId) throws Exception {

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAllTransactions/" + bic)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            List<JsonResponseDto.Result>  results = response.result;
            //     List<DisableAccountOwner> result =response.result.stream().map(ac -> ((DisableAccountOwner.Contract)ac).data).collect(Collectors.toList());
        }
        return null;
    }


    public AccountOwnerSummaryWrapperDto listAccountOwnerSummary(String centralBank) throws Exception {
        AccountOwnerSummaryWrapperDto accountOwnerSummaryWrapperDto = client
                .get()
                .uri("/api/search/getAccountOwnerSummary/" + centralBank)
                .retrieve()
                .bodyToMono(AccountOwnerSummaryWrapperDto.class)
                .block();

        return accountOwnerSummaryWrapperDto;
    }

    public AccountOwnerDto getAccountOwner(String shortName) throws Exception {
        AccountOwnerDto accountOwnerDto = new AccountOwnerDto();

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/search/getAccountOwners/" + shortName)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200) {

            try {
                org.apache.commons.beanutils.BeanUtils.populate(accountOwnerDto, (Map)response.result.get(0).payload);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return accountOwnerDto;

    }

    public BalanceResponseDto getBalance(BalanceRequestDto requestDto) throws Exception {
        BalanceResponseDto balanceRequestDto = client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/cbdc/balance/")
                                .queryParam("bankParty", requestDto.getBankParty())
                                .queryParam( "accountId", requestDto.getAccountId())
                                .queryParam("currency", requestDto.getCurrency())
                                .build())
                .retrieve()
                .bodyToMono(BalanceResponseDto.class)
                .block();

        return balanceRequestDto;
    }

    public List<BalanceResponseDto> getBalances(BalanceRequestDto requestDto) throws Exception {

        List<BalanceResponseDto> balances  = listParticipantBankAccounts(requestDto.getBankParty())
                .parallelStream()
                .map( accountOwnerResponseDto -> {
                    try {
                        BalanceResponseDto balanceResponseDto =  this.getBalance( new BalanceRequestDto(  accountOwnerResponseDto.getShortName(),
                                            accountOwnerResponseDto.getAccountId(),
                                Currency.valueOf(accountOwnerResponseDto.getCurrency()) ));

                        balanceResponseDto.setBankName(accountOwnerResponseDto.getShortName());

                        return balanceResponseDto;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        ).collect(Collectors.toList());

            return balances;
    }

    public List<CsdBalanceResponseDto> getCsdBalances(CsdBalanceRequestDto requestDto) throws Exception {
        // http://34.143.143.223:4030/api/dvp/balance?bankParty=SGBANK1&accountId=string&isin=string

            Flux<CsdBalanceResponseDto> accountOwnerFlux = client
                    .get()
                    .uri( "/api/dvp/balance?bankParty=" + requestDto.getBankParty()
                            +  "&accountId=" + requestDto.getAccountId()
                            + ( "&isin=" + requestDto.getIsin())  )
//
                    //)
//                    .uri("/api/dvp/balance?bankParty=SGBANK1&accountId=string&isin=string")
                    .retrieve()
                    .bodyToFlux(CsdBalanceResponseDto.class);

            List<CsdBalanceResponseDto> response = accountOwnerFlux.collectList().block();

            return response;
    }


    public TransactionResponseDto enableOrDisableAccOwner(DisableAccountOwnerResponseDto disableAccountOwnerResponseDto){

        TransactionResponseDto transactionResponseDto = client
                .post()
                .uri("/api/admin/enableOrDisableAccOwner")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(disableAccountOwnerResponseDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();

        return transactionResponseDto;

    }
    public TransactionResponseDto onboardCentralbank(CentralBankDto centralBankDto){

        TransactionResponseDto transactionResponseDto = client.post()
                .uri("/api/admin/onboardCentralBank")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(centralBankDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();

        return transactionResponseDto;

    }
    public TransactionResponseDto onboardAccountOwner(AccountOwnerDto accountOwnerDto){

        TransactionResponseDto transactionResponseDto = client.post()
                .uri("/api/admin/onboardBank")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(accountOwnerDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();

        return transactionResponseDto;

    }
    public TransactionResponseDto onboardBankAccountId(CbdcAccountDto cbdcAccountDto){

        TransactionResponseDto transactionResponseDto = client.post()
                .uri("/api/admin/createCbdcAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cbdcAccountDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();

        return transactionResponseDto;

    }

    public RtgsCallbackDto deposit(RequestDepositDto requestDepositDto){

        RtgsCallbackDto rtgsCallbackDto = client.post()
                .uri("/api/cbdc/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDepositDto)
                .retrieve()
                .bodyToMono(RtgsCallbackDto.class)
                .block();

        return rtgsCallbackDto;

    }
    public RtgsCallbackDto withdraw(RequestWithdrawDto requestDepositDto){

        RtgsCallbackDto rtgsCallbackDto = client.post()
                .uri("/api/cbdc/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDepositDto)
                .retrieve()
                .bodyToMono(RtgsCallbackDto.class)
                .block();

             return rtgsCallbackDto;

    }
    public RtgsCallbackDto transfer(RequestTransferDto requestTransferDto){

        RtgsCallbackDto rtgsCallbackDto = client.post()
                .uri("/api/cbdc/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestTransferDto)
                .retrieve()
                .bodyToMono(RtgsCallbackDto.class)
                .block();

        return rtgsCallbackDto;

    }

    public TransactionResponseDto approveOnnboardingRequest(AccountOwnerOnboardingRequest accountOwnerOnboardingRequest){
        return this.approveOrRejectOnnboardingRequest(accountOwnerOnboardingRequest,  "/api/admin/approveOnboardingRequest");
    }



    public TransactionResponseDto rejectOnnboardingRequest(AccountOwnerOnboardingRequest accountOwnerOnboardingRequest){
        return this.approveOrRejectOnnboardingRequest(accountOwnerOnboardingRequest, "/api/admin/rejectOnboardingRequest");
    }

    public TransactionResponseDto approveOrRejectOnnboardingRequest(AccountOwnerOnboardingRequest accountOwnerOnboardingRequest, String url){
        TransactionResponseDto transactionResponseDto = client
                .post()
                .uri("/api/admin/approveOnboardingRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(accountOwnerOnboardingRequest)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();
        return transactionResponseDto;

    }

    public List<AccountOwnerOnboardingResponse> listAccountOwnerOnboardingRequest(String centralBankUser) throws Exception {
        return this.listAccountOwnerRequest(centralBankUser,"/api/search/onboardingRequests/");
    }

    public List<AccountOwnerOnboardingResponse> listAccountOwnerApprovedRequest(String centralBankUser) throws Exception {
        return this.listAccountOwnerRequest(centralBankUser,"/api/search/approvedOnboardingRequests/");
    }


    public List<AccountOwnerOnboardingResponse> listAccountOwnerCancelledRequest(String centralBankUser) throws Exception {
        return this.listAccountOwnerRequest(centralBankUser,"/api/search/cancelleddOnboardingRequests/");
    }

    public List<AccountOwnerOnboardingResponse> listAccountOwnerRequest(String centralBankUser, String url) throws Exception {
        List<AccountOwnerOnboardingResponse> accountOwnerResponseDtos = new ArrayList();

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri(url + centralBankUser)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();

        if(response.status == 200){
            if(!response.result.isEmpty()) {
                accountOwnerResponseDtos = response.result.stream().map(
                        result -> {
                            Map payload = (Map) result.payload;
                            AccountOwnerOnboardingResponse accountOwnerResponseDto = new AccountOwnerOnboardingResponse();
                            try {
                                accountOwnerResponseDto =  onboardingResponseDto(payload);
                                accountOwnerResponseDto.setIsLocal((Boolean) payload.get("isLocal"));
                                org.apache.commons.beanutils.BeanUtils.populate(accountOwnerResponseDto, payload);

                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (ConversionException conversionException){
                              accountOwnerResponseDto.setStatus(ProposalStatus.valueOf( String.valueOf(payload.getOrDefault("status", "INITIATED"))));

                            }
                            return accountOwnerResponseDto;
                        }
                ).collect(Collectors.toList());
            }
        }
        return accountOwnerResponseDtos;
    }


    public List<RtgsTransactionResponseProposalDto> listPendingWithdrawals(String centralBankUser, String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/withdrawalRequests/", TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listCancelledWithdrawals(String centralBankUser,String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/cancelledWithdrawalRequests/", TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listApprovedWithdrawals(String centralBankUser, String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/approvedWithdrawalRequests/", TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listPendingDeposits(String centralBankUser, String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/depositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listCancelledDeposits(String centralBankUser, String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/cancelledDepositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listApprovedDeposits(String centralBankUser, String userName) throws Exception {
        return this.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/approvedDepositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listRtgsTransactionRequestProposals(String centralBankUser, String url,
                                                                                        TransactionType transactionType, String userName) throws Exception {
        List<RtgsTransactionResponseProposalDto> depositResponseDtos = new ArrayList();

        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri(url + centralBankUser)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();


//        Predicate<> byParticipantBank = (transaction) ->transaction.getPartyString().split("::")[0].equals(centralBankUser) ;
//        Predicate<AccountOwnerSummaryDto> allTrans = (transaction) -> transaction != null   ;
//
//        Predicate filterBy = null;
//        if(centralBankUser.equals( cbcdOperator )) {
//            filterBy =  allTrans;
//        } else {
//            filterBy = byParticipantBank;
//        }

        if(response.status == 200){
            depositResponseDtos = response.result.stream().map(
                    result -> {
                        Map payload = (Map)result.payload;

                        String shortName = "";
                        if( (Map)payload.get("toAccount") == null){
                            shortName =     String.valueOf( ((Map)(((Map)((Map) (Map)payload.get("owner"))).get("accountOwner"))).get("shortName"));
                        } else {
                             shortName =     String.valueOf( ((Map)(((Map)((Map) (Map)payload.get("toAccount"))).get("accountOwner"))).get("shortName"));

                        }



                        if( (userName.equals("MAS") || userName.equals("BI") ) || shortName.equalsIgnoreCase(userName)) {
                            RtgsTransactionResponseProposalDto rtgsTransactionResponseProposalDto = new RtgsTransactionResponseProposalDto();


                            rtgsTransactionResponseProposalDto.setCurrency(Currency.valueOf(String.valueOf(payload.get("currency"))));
                            rtgsTransactionResponseProposalDto.setAmount(new BigDecimal((String) payload.get("amount")));
                            rtgsTransactionResponseProposalDto.setType(transactionType);
                            Map cbdcAccountMap = null;
                          //  if (transactionType.equals(TransactionType.DEPOSIT)) {
                                CBDCAccount cbdcAccount = new CBDCAccount();

                                if( (Map)payload.get("toAccount") == null){
                                     cbdcAccountMap = ((Map) payload.get("owner"));
                                } else {
                                     cbdcAccountMap = ((Map) payload.get("toAccount"));
                                }

                                cbdcAccount.setAccountOwner(mapToDto(cbdcAccountMap));
                                cbdcAccount.setAccountId(String.valueOf(cbdcAccountMap.get("accountId")));

                                rtgsTransactionResponseProposalDto.setAccount(cbdcAccount);
//                            } else if (transactionType.equals(TransactionType.WITHDRAWAL)) {
//                                if( (Map)payload.get("toAccount") == null){
//                                    cbdcAccountMap = ((Map) payload.get("owner"));
//                                } else {
//                                    cbdcAccountMap = ((Map) payload.get("toAccount"));
//                                }
//                                rtgsTransactionResponseProposalDto.setAccount(mapToCBDCAccount(cbdcAccountMap));
//                            }


                            return rtgsTransactionResponseProposalDto;
                        }
                        return null;
                    }
            ).collect(Collectors.toList());
        }
        depositResponseDtos.removeAll( Collections.singleton(null));
        return depositResponseDtos;
    }

    private AccountOwnerDto mapToDto(Map payload){
        AccountOwnerDto accountOwnerDto = new AccountOwnerDto();
        try {
            org.apache.commons.beanutils.BeanUtils.populate(accountOwnerDto, (Map)payload.get("accountOwner") );

            accountOwnerDto.setSponsorParty(  (String) ((Map)payload.get("accountOwner")).get("sponsor") );
            accountOwnerDto.setCentralBankParty(   (String) ((Map)payload.get("accountOwner")).get("centralBank")  );
            accountOwnerDto.setLocal( (Boolean) ((Map)payload.get("accountOwner")).get("isLocal") );

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return accountOwnerDto;
    }

    private AccountOwnerOnboardingResponse onboardingResponseDto(Map payload){
        AccountOwnerOnboardingResponse onbaordingDto = new AccountOwnerOnboardingResponse();
            onbaordingDto.setCentralBank(  String.valueOf(payload.get("centralBank")).substring(0,3)  );
            onbaordingDto.setCurrency( Currency.valueOf(String.valueOf(payload.get("currency"))) );
            onbaordingDto.setShortName( String.valueOf(payload.get("shortName")));
            onbaordingDto.setStatus( ProposalStatus.valueOf(String.valueOf(payload.get("status"))));
        return onbaordingDto;
    }

    public List<PvpIntegrationDto> listCompletedPvps(String bankShortName) throws Exception {
        return listPvps(bankShortName, "/api/search/pvp/completed/");
    }

    public List<PvpIntegrationDto> listCancelledPvps(String bankShortName) throws Exception {
        return listPvps(bankShortName, "/api/search/pvp/cancelled/");
    }

    public List<PvpIntegrationDto> listPvps(String bankShortName, String url) throws Exception {

        List<PvpIntegrationDto> pvps = null;
        Flux<Map> accountOwnerFlux = client
                .get()
                .uri( url  + bankShortName)
                .retrieve()
                .bodyToFlux(Map.class);

        List<Map> response = accountOwnerFlux.collectList().block();

     //   if(response.status == 200){

            pvps = response.stream().map(
                    result -> {
                        PvP pvp = new PvP();
                        Map payload = (Map)result.get("data");
                        String contractId =  (String) ((Map)result.get("id")).get("contractId");

                        PvPLeg leg1 = new PvPLeg();
                          CBDCAccount leg1FromAccount = new CBDCAccount();

                          CBDCAccount leg1ToAccount = new CBDCAccount();
                          BigDecimal leg1Amount = null;
                          AccountOwnerDto leg1FromAccountOwnerDto = new AccountOwnerDto();
                          AccountOwnerDto leg1ToAccountOwnerDto = new AccountOwnerDto();

                          String leg1AccountId = new String();

                        CBDCAccount leg2FromAccount = new CBDCAccount();
                        CBDCAccount leg2ToAccount = new CBDCAccount();
                        BigDecimal leg2Amount = null;
                        AccountOwnerDto leg2FromAccountOwnerDto = new AccountOwnerDto();
                        AccountOwnerDto leg2ToAccountOwnerDto = new AccountOwnerDto();




                        PvPLeg leg2 = new PvPLeg();


                        try {


                            BeanUtils.populate(leg1FromAccountOwnerDto,   (Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner") );
                            leg1FromAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("sponsor") );
                            leg1FromAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("centralBank")  );
                            leg1FromAccountOwnerDto.setLocal( (Boolean) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("isLocal")  );


                            BeanUtils.populate(leg1ToAccountOwnerDto,  (Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner"));
                            leg1ToAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("sponsor") );
                            leg1ToAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("centralBank")  );
                            leg1ToAccountOwnerDto.setLocal( (Boolean) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("isLocal")  );



                            leg1Amount =  new BigDecimal(  String.valueOf (((Map) payload.get("leg1")).get("amount")) );


                            leg1FromAccount.setAccountOwner(leg1FromAccountOwnerDto);
                            leg1ToAccount.setAccountOwner(leg1ToAccountOwnerDto);
                            leg1.setFromAccount(leg1FromAccount);
                            leg1.setToAccount(leg1ToAccount);
                            leg1.setAmount(leg1Amount);

                            leg1FromAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg1")).get("fromAccount")).get("accountId")));
                            leg1ToAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg1")).get("toAccount")).get("accountId")));



                           BeanUtils.populate(leg2FromAccountOwnerDto, (Map) ((Map)((Map)payload.get("leg2")).get("fromAccount")).get("accountOwner") );
                            leg2FromAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg2")).get("fromAccount")).get("accountOwner")).get("sponsor") );
                            leg2FromAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg2")).get("fromAccount")).get("accountOwner")).get("centralBank")  );
                            leg2FromAccountOwnerDto.setLocal(  (Boolean) ((Map) ((Map)((Map)payload.get("leg2")).get("fromAccount")).get("accountOwner")).get("isLocal") );


                            BeanUtils.populate(leg2ToAccountOwnerDto,  (Map) ((Map)((Map)payload.get("leg2")).get("toAccount")).get("accountOwner"));
                            leg2ToAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg2")).get("toAccount")).get("accountOwner")).get("sponsor") );
                            leg2ToAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg2")).get("toAccount")).get("accountOwner")).get("centralBank")  );
                            leg2ToAccountOwnerDto.setLocal( (Boolean) ((Map) ((Map)((Map)payload.get("leg2")).get("toAccount")).get("accountOwner")).get("isLocal") );

                            leg2Amount =  new BigDecimal(  String.valueOf ( String.valueOf (((Map) payload.get("leg2")).get("amount")) ));

                            leg2FromAccount.setAccountOwner(leg2FromAccountOwnerDto);
                            leg2ToAccount.setAccountOwner(leg2ToAccountOwnerDto);
                            leg2.setFromAccount(leg2FromAccount);
                            leg2.setToAccount(leg2ToAccount);
                            leg2.setAmount(leg2Amount);

                            leg2FromAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg2")).get("fromAccount")).get("accountId")));
                            leg2ToAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg2")).get("toAccount")).get("accountId")));

                                pvp.setLeg1(leg1);
                                pvp.setLeg2(leg2);
                                pvp.setTradeId( String.valueOf(payload.get("tradeId")) );
                                pvp.setContractId( contractId);
                                pvp.setStatus( String.valueOf(payload.get("status")));

            log.info("leg1.getAmount():{}:leg2.getAmount(){}", leg1.getAmount(), leg2.getAmount());
                                pvp.setRates(  String.valueOf( leg1.getAmount().divide(leg2.getAmount(), 12, RoundingMode.HALF_UP) ) );

                       } catch (IllegalAccessException e) {
                           e.printStackTrace();
                       } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        //PvP
                        PvpIntegrationDto pvpIntegrationDto = new PvpIntegrationDto();
                    pvpIntegrationDto.setPvp(pvp);
                        return pvpIntegrationDto ;
                    }
            ).collect(Collectors.toList());
      //  }
        return pvps;
    }

    public List<PvPProposal> listPvpProposals(String bankShortName) throws Exception {
        List<PvPProposal> pvPProposals = null;
        Mono<JsonResponseDto> accountOwnerFlux = client
                .get()
                .uri("/api/pvpProposals/" + bankShortName)
                .retrieve()
                .bodyToMono(JsonResponseDto.class);

        JsonResponseDto response = accountOwnerFlux.block();
        if(response.status == 200){
            pvPProposals = response.result.stream().map(
                    result -> {
                        
                        PvPProposal pvPProposal = new PvPProposal();
                        return pvPProposal ;
                    }
            ).collect(Collectors.toList());
        }
        return pvPProposals;
    }

    public String fundPvp(FundPvpDto fundPvpDto) {

        String  fundPvp = client
                .post()
                .uri("/api/cbdc/pvp/fund")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(fundPvpDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return fundPvp;
    }

    public List<DvpIntegrationDto> listCompletedDvps(String bankShortName) throws Exception {
        return listDvps(bankShortName, "/api/search/dvp/completed/");
    }

    public List<DvpIntegrationDto> listCancelledDvps(String bankShortName) throws Exception {
        return listDvps(bankShortName, "/api/search/dvp/cancelled/");
    }

    public List<DvpIntegrationDto> listDvps(String bankShortName, String url) throws Exception {

        List<DvpIntegrationDto> dvps = null;
        Flux<Map> accountOwnerFlux = client
                .get()
                .uri( url  + bankShortName)
                .retrieve()
                .bodyToFlux(Map.class);

        List<Map> response = accountOwnerFlux.collectList().block();

        //   if(response.status == 200){

        dvps = response.stream().map(
                result -> {
                    DvP dvP = new DvP();

                    Map payload = (Map)result.get("data");
                    String contractId =  (String) ((Map)result.get("id")).get("contractId");

                    DvPCBDCLeg leg1 = new DvPCBDCLeg();

                    CBDCAccount leg1FromAccount = new CBDCAccount();

                    CBDCAccount leg1ToAccount = new CBDCAccount();
                    BigDecimal leg1Amount = null;
                    AccountOwnerDto leg1FromAccountOwnerDto = new AccountOwnerDto();
                    AccountOwnerDto leg1ToAccountOwnerDto = new AccountOwnerDto();

                    String leg1CommittedToken = new String();



                    CSDAccount leg2FromAccount = new CSDAccount();
                    CSDAccountOwner leg2FromAccountOwnerDto = new CSDAccountOwner();
                    String leg2FromAccountAccountId = "";

                    CSDAccount leg2ToAccount = new CSDAccount();
                    CSDAccountOwner leg2ToAccountOwnerDto = new CSDAccountOwner();
                    String leg2ToAccountAccountId = "";

                    String leg2Isin = null;
                    Double leg2Quantity = null;
                    String committedToken = null;

                    DvPCSDLeg leg2 = new DvPCSDLeg();

                    try {

                        BeanUtils.populate(leg1FromAccountOwnerDto,   (Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner") );
                        leg1FromAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("sponsor") );
                        leg1FromAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("centralBank")  );
                        leg1FromAccountOwnerDto.setLocal( (Boolean) ((Map) ((Map)((Map)payload.get("leg1")).get("fromAccount")).get("accountOwner")).get("isLocal")  );

                        BeanUtils.populate(leg1ToAccountOwnerDto,  (Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner"));
                        leg1ToAccountOwnerDto.setSponsorParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("sponsor") );
                        leg1ToAccountOwnerDto.setCentralBankParty(   (String) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("centralBank")  );
                        leg1ToAccountOwnerDto.setLocal( (Boolean) ((Map) ((Map)((Map)payload.get("leg1")).get("toAccount")).get("accountOwner")).get("isLocal")  );


                        leg1Amount =  new BigDecimal(  String.valueOf (((Map) payload.get("leg1")).get("amount")) );

                        leg1FromAccount.setAccountOwner(leg1FromAccountOwnerDto);
                        leg1ToAccount.setAccountOwner(leg1ToAccountOwnerDto);
                        leg1.setFromAccount(leg1FromAccount);
                        leg1.setToAccount(leg1ToAccount);
                        leg1.setAmount(leg1Amount);

                        Map l1 = ( Map<String,Object>)payload.get("leg1");
                        if( l1.get("committedToken") !=null) {
                            leg1.setCommittedToken((String) ((Map) l1.get("committedToken")).get("contractId"));
                        } else {
                            leg1.setCommittedToken(null);
                        }

                        leg1FromAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg1")).get("fromAccount")).get("accountId")));
                        leg1ToAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg1")).get("toAccount")).get("accountId")));

                        BeanUtils.populate(leg2FromAccountOwnerDto, (Map) ((Map)((Map)payload.get("leg2")).get("fromAccount")).get("accountOwner") );

                        BeanUtils.populate(leg2ToAccountOwnerDto,  (Map) ((Map)((Map)payload.get("leg2")).get("toAccount")).get("accountOwner"));


                        leg2FromAccount.setAccountOwner(leg2FromAccountOwnerDto);
                        leg2ToAccount.setAccountOwner(leg2ToAccountOwnerDto);

                        leg2.setFromAccount(leg2FromAccount);
                        leg2.setToAccount(leg2ToAccount);

                        leg2Isin =  (String)((Map)payload.get("leg2")).get("isin");
                        leg2Quantity = (Double)((Map)payload.get("leg2")).get("quantity");

                        Map l2 = ( Map<String,Object>)payload.get("leg2");
                        if( l2.get("committedToken") !=null) {
                            committedToken = (String) ((Map) l2.get("committedToken")).get("contractId");
                        } else {
                            committedToken = null;
                        }

                        leg2.setIsin(leg2Isin);
                        leg2.setQuantity(leg2Quantity);
                        leg2.setCommittedToken(committedToken);


                        leg2FromAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg2")).get("fromAccount")).get("accountId")));
                        leg2ToAccount.setAccountId( String.valueOf ( ( (Map) ((Map) payload.get("leg2")).get("toAccount")).get("accountId")));

                        dvP.setLeg1(leg1);
                        dvP.setLeg2(leg2);
                        dvP.setTradeId( String.valueOf(payload.get("tradeId")));
                        dvP.setStatus( String.valueOf(payload.get("status")));
                        dvP.setContractId(contractId);


                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    //PvP
                    DvpIntegrationDto pvpIntegrationDto = new DvpIntegrationDto();
                    pvpIntegrationDto.setDvp(dvP);
                    return pvpIntegrationDto ;
                }
        ).collect(Collectors.toList());
        //  }
        return dvps;
    }

    public String fundDvp(FundDvpDto fundPvpDto) {

        String  fundPvp = client
                .post()
                .uri("/api/cbdc/dvp/fund")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(fundPvpDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return fundPvp;
    }


    public TransactionResponseDto acceptDvPTrade(AcceptDvPTradeDto acceptDvPTradeDto) {

        TransactionResponseDto  fundPvp = client
                .post()
                .uri("/api/acceptDvpTrade")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(acceptDvPTradeDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();

        return fundPvp;
    }

    public List<DomainResponseDto> listConnectedDomains() throws Exception {
        return this.listDomains("/api/search/listConnectedDomains");
    }

    public List<DomainResponseDto> listAllDomains() throws Exception {
        return this.listDomains("/api/search/listAllDomains");
    }

    public List<DomainResponseDto> listDomains(String urls) throws Exception {
        Flux<DomainResponseDto> accountOwnerFlux = client
                .get()
                .uri(urls)
                .retrieve()
                .bodyToFlux(DomainResponseDto.class);
        return accountOwnerFlux.collectList().block();
    }

    public String connectDomains(DomainDto domainDto) {
        return this.connectCbdc(domainDto, "/api/domain/connectDomain");
    }

    public String disConnectDomains(DomainDto domainDto) {
        return this.connectCbdc(domainDto, "/api/domain/disconnectDomain");
    }


    public String connectCbdc(DomainDto domainDto, String url) {

        String  connectCbdc = client
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(domainDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return connectCbdc;
    }


    public static void main(String[] args) {
       JsonClientWrapper.testListParticipantBankAccounts("DBSSGDXXX1234");
    }

    public static void testListParticipantBanks(String centralBank){
        var clientWrapper = new JsonClientWrapper("localhost" , "8081");
        try {
            System.out.println("\n\n==============  Sending Request ==============");

            List<AccountOwnerResponseDto> x =  clientWrapper.listParticipantBanks(centralBank);
            System.out.println( );
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }
    public static void testListParticipantBankAccounts(String bic){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            List<AccountOwnerResponseDto> jsonResponseDtoList  =
                    clientWrapper.listParticipantBankAccounts(bic);
            System.out.println(jsonResponseDtoList);
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }

    public static void testListCentralBankTransactions(String centralBank){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            List<TransactionReceiptDto> jsonResponseDtoList  =
                    clientWrapper.listCentralBankTransactions(centralBank);
            System.out.println(jsonResponseDtoList);
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }
    public static void testListParticipantBankTransactions(String bic){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            List<TransactionReceiptDto> jsonResponseDtoList  =
                    clientWrapper.listParticipantBankTransactions(bic);

            System.out.println(jsonResponseDtoList);
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }

    public static void testListParticipantBankAccountTransactions(String bic, String accountId){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            List<TransactionReceiptDto> jsonResponseDtoList  =
                    clientWrapper.listParticipantBankTransactions(bic);

            System.out.println(jsonResponseDtoList);
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }

    public static void testOnboardAccountOwner(){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            TransactionResponseDto transactionResponseDto =
                    clientWrapper.onboardAccountOwner( new AccountOwnerDto( "PNB2",
                    "PNBSSGSGXXX", "pnb",
                    "BSP", "SGD", "PNB1"));

            System.out.println(transactionResponseDto.getTransactionId());
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");

    }

    public static void testOnboardBankAccountId(String bic, String centralBankParty){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            TransactionResponseDto transactionResponseDto =
                    clientWrapper.onboardBankAccountId( new CbdcAccountDto(bic));

            System.out.println(transactionResponseDto.getTransactionId());
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");
    }
    public static void testOnboardCentralBank(){
        var clientWrapper = new JsonClientWrapper("localhost","8081");
        try {
            TransactionResponseDto transactionResponseDto =
                    clientWrapper .onboardCentralbank( new CentralBankDto("BSP", "SGD") );

            System.out.println(transactionResponseDto.getTransactionId());
        } catch (Exception e) {
            System.out.println("\n\n==============  Error ==============");
            System.out.println(e);
        }
        System.out.println("\n\n==============  Staphed ==============");

    }




}