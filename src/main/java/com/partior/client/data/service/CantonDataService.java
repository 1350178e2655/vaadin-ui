package com.partior.client.data.service;

import com.partior.client.dto.*;
import com.partior.client.dto.enums.Currency;
import com.partior.client.dto.enums.TransactionType;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.util.JsonClientWrapper;
import com.partior.client.views.transfers.PvpTradesForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;

@Slf4j
@Service
public class CantonDataService {

    @Autowired
    final JsonClientWrapper jsonClientWrapper;


    final String cbdcOperator;


    public CantonDataService(JsonClientWrapper jsonClientWrapper,  @Value("${cbdc.operator:MAS}")String cbdcOperator ) {
        log.info("CBDC Operator:{}", cbdcOperator);
        this.jsonClientWrapper = jsonClientWrapper;
        this.cbdcOperator =  cbdcOperator;
    }

    public List<AccountOwnerResponseDto> listAccountOwners(String centralBankUser) throws Exception {
        return jsonClientWrapper.listParticipantBanks(centralBankUser);
    }

    public List<AccountOwnerResponseDto> listParticipantBankAccounts(String bic) throws Exception {
        return jsonClientWrapper.listParticipantBankAccounts(bic);

    }

    public List<CentralBankDto> listCentralBanks() throws Exception {
        return jsonClientWrapper.listCentralBanks();

    }

    public TransactionResponseDto onboardAccountOwner(AccountOwnerDto accountOwnerDto) {
        return jsonClientWrapper.onboardAccountOwner(accountOwnerDto);
    }

    public TransactionResponseDto onboardCentralbank(CentralBankDto centralBankDto) {
        return  jsonClientWrapper.onboardCentralbank(centralBankDto);
    }

    public List<AccountOwnerResponseDto> listDisabledParticipantBanks(String centralBankUser) throws Exception {
        return jsonClientWrapper.listDisabledParticipantBanks(centralBankUser);
    }

    public TransactionResponseDto enableOrDisableAccOwner(DisableAccountOwnerResponseDto accountOwnerResponseDto) {
        return jsonClientWrapper.enableOrDisableAccOwner(accountOwnerResponseDto);
    }

    public List<CsdBalanceResponseDto> getCsdBalances(CsdBalanceRequestDto requestDto) throws Exception {
        return jsonClientWrapper.getCsdBalances(requestDto);
    }

    public TransactionResponseDto  onboardBankAccountId(CbdcAccountDto cbdcAccountDto) {
        return jsonClientWrapper.onboardBankAccountId(cbdcAccountDto);
    }

    public RtgsCallbackDto deposit( RequestDepositDto requestDepositDto){
        return jsonClientWrapper.deposit(requestDepositDto);
    }

    public RtgsCallbackDto withdraw(RequestWithdrawDto requestWithdrawDto){
        return jsonClientWrapper.withdraw(requestWithdrawDto);
    }

    public RtgsCallbackDto transfer(RequestTransferDto requestTransferDto){
        return jsonClientWrapper.transfer(requestTransferDto);
    }

    public List<BalanceResponseDto> getBalances(BalanceRequestDto balanceRequestDto) throws Exception {
           return jsonClientWrapper.getBalances(balanceRequestDto);
    }

    public List<DashboardAccountOverviewDto> listCbdcDashboardAccountOverview(String centralBank) throws Exception {
        String userBankName = getUserBankName();
        List<AccountOwnerResponseDto>  bankInfos =  jsonClientWrapper.listParticipantBanks(centralBank);
        AccountOwnerSummaryWrapperDto summaryWrapperDto =  jsonClientWrapper.listAccountOwnerSummary(centralBank);

        Predicate<AccountOwnerSummaryDto> byParticipantBank = (transaction) ->transaction.getPartyString().split("::")[0].equals(userBankName) ;
        Predicate<AccountOwnerSummaryDto> allTrans = (transaction) -> transaction != null   ;

        Predicate<AccountOwnerSummaryDto> filterBy = null;
        if(userBankName.equals( getCdbcOperator() )) {
            filterBy =  allTrans;
        } else {
            filterBy = byParticipantBank;
        }


        List<DashboardAccountOverviewDto> dashboardAccountOverviewDtoList = summaryWrapperDto.getAccountOwnerSummaries().stream()
               .filter(filterBy)
               .map(
                       accountOverviewDto -> {
                           AccountOwnerResponseDto bankInfo = getBank(accountOverviewDto.getPartyString().split("::")[0], bankInfos);

                           DashboardAccountOverviewDto dashboardAccountOverviewDto = new DashboardAccountOverviewDto();
                           dashboardAccountOverviewDto.setBalance(  accountOverviewDto.getTotalBalance() .toString() );
                           dashboardAccountOverviewDto.setDeposit( accountOverviewDto.getTotalDeposits().toString() );
                           dashboardAccountOverviewDto.setSent( accountOverviewDto.getTotalSent().toString() );
                           dashboardAccountOverviewDto.setReceived ( accountOverviewDto.getTotalReceived().toString() );
                           dashboardAccountOverviewDto.setWithdrawals( String.valueOf(accountOverviewDto.getTotalWithdrawals()));

                           dashboardAccountOverviewDto.setBankName( UIUtils.ENTITY_NAME.get(accountOverviewDto.getPartyString().substring(0,3)) );
                           dashboardAccountOverviewDto.setBic(accountOverviewDto.getPartyString().split("::")[0]);
                           dashboardAccountOverviewDto.setAccountType( bankInfo.isLocal()?"Domestic":"Foreign" );

                           return dashboardAccountOverviewDto ;
                       }

               ).collect(Collectors.toList());



        return dashboardAccountOverviewDtoList;
    }

    public AccountOwnerResponseDto getBank(String bankName, List<AccountOwnerResponseDto>  bankInfos  ){
        return bankInfos.stream().filter(bank-> bankName.equals(bank.getShortName()))
                .findFirst().orElse(null);
    }

    public List<DashboardTransactionHistoryDto> listAggregatedAccountIdTransactionHistory(String centralBank, List<DashboardAccountOverviewDto> dashboardAccountOverviewDtoList ) throws Exception {

        String userBankName = getUserBankName();
        List<AccountOwnerResponseDto> accountIds =  new ArrayList<>();
        if( userBankName.equals( centralBank ) ) {
            for (DashboardAccountOverviewDto dtos: dashboardAccountOverviewDtoList){
                accountIds.addAll(listParticipantBankAccounts(dtos.getBic()));
            }
        } else {
            accountIds =  listParticipantBankAccounts(userBankName);
        }


        List<DashboardTransactionHistoryDto> dashboardTransactionHistoryDtos = accountIds.stream().map(
                accountId -> {
                    DashboardTransactionHistoryDto transactionHistoryDto = new DashboardTransactionHistoryDto();
                    try {
                        BalanceResponseDto balanceResponseDto
                                =    jsonClientWrapper.getBalance(new BalanceRequestDto( accountId.getShortName(),
                                accountId.getAccountId(), Currency.valueOf(accountId.getCurrency()) ));

                        transactionHistoryDto.setAccountId( accountId.getAccountId());
                        transactionHistoryDto.setCurrency(accountId.getCurrency());
                        transactionHistoryDto.setQuantity(balanceResponseDto.getAmount()
                                .setScale(3, RoundingMode.HALF_UP).doubleValue() );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return transactionHistoryDto;
                }
        ).collect(Collectors.toList());



        return dashboardTransactionHistoryDtos;

    }

    public List<DashboardTransactionHistoryDto> listCbdcDashboardTransactionHistory(String centralBank, String accountId) throws Exception {
        List<TransactionReceiptDto> transactions = new ArrayList<>();
        String userBankName = getUserBankName();

//         if( userBankName.equals( getCdbcOperator() ) ) {
//             transactions = jsonClientWrapper.listCentralBankTransactions(centralBank);
//          } else {
             transactions = jsonClientWrapper.listParticipantBankTransactions(centralBank);
//         }

        Predicate<TransactionReceiptDto> byAccountId = (tranHistoDto) -> tranHistoDto.getToAccountId().equals(accountId);
        Predicate<TransactionReceiptDto> allTrans = (tranHistoDto) -> tranHistoDto != null   ;

        Predicate<TransactionReceiptDto> filterBy = accountId!=null?byAccountId:allTrans;

        List<DashboardTransactionHistoryDto> dashboardTransactionHistoryDtos = transactions.stream()
                .filter(filterBy)
                .map(
                        dto -> {
                            DashboardTransactionHistoryDto dashboardTransactionHistoryDto = new DashboardTransactionHistoryDto();

                            dashboardTransactionHistoryDto.setPayor(dto.getFromParty().split("::")[0]);
                            dashboardTransactionHistoryDto.setQuantity(Precision.round(Double.valueOf( dto.getQuantity()),3));
                            dashboardTransactionHistoryDto.setPayee(dto.getToParty().split("::")[0]);

                            dashboardTransactionHistoryDto.setStatus( dto.getStatus() );

                            dashboardTransactionHistoryDto.setTimeStamp( dto.getTimestamp());
                            dashboardTransactionHistoryDto.setTransactionId("-");

                            dashboardTransactionHistoryDto.setToAccountId(dto.getToAccountId());
                            dashboardTransactionHistoryDto.setAccountId(dto.getToAccountId());
                            dashboardTransactionHistoryDto.setCurrency(dto.getCurrency());
                            dashboardTransactionHistoryDto.setTxnType( dto.getTxnType());

                            return dashboardTransactionHistoryDto ;
                        }

                ).collect(Collectors.toList());


//        Map<DashboardTransactionHistoryDto, Integer> o =
//        dashboardTransactionHistoryDtos.stream().collect(
//            groupingBy( DashboardTransactionHistoryDto::getToAccountId,
//                    summarizingInt(DashboardTransactionHistoryDto::getReceived)
//        ));

        // Serum

        return dashboardTransactionHistoryDtos;
    }

    public List<DashboardTransactionHistoryDto> listParticipantBankTransactionHistory(String bic) throws Exception {
        List<TransactionReceiptDto> transactions = jsonClientWrapper.listParticipantBankTransactions(bic);
        List<DashboardTransactionHistoryDto> dashboardTransactionHistoryDtos = transactions.stream()
                .map( transaction ->{
                    return  new DashboardTransactionHistoryDto();
                }).collect(Collectors.toList());

        return dashboardTransactionHistoryDtos;
    }

    private List<TransactionReceiptDto> listCentralBankTransactions(String centralBank) throws Exception {
        return jsonClientWrapper.listCentralBankTransactions(centralBank);
    }

    public BalanceResponseDto getBalance(BalanceRequestDto requestDto) throws Exception {
        return jsonClientWrapper.getBalance(requestDto);
    }

    public String getUserBankName(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if("cdbc".equals(userName) || "admin".equals(userName) || "MAS".equals(userName) || "BOI".equals(userName) || "BI".equals(userName)){
            return getCdbcOperator() ; // "mas";
        } else{
            return userName;
        }
    }

    public String getCdbcOperator(){
        return this.cbdcOperator;
    }


    public TransactionResponseDto approveOnnboardingRequest(AccountOwnerOnboardingRequest accountOwnerOnboardingRequest){
        return jsonClientWrapper.approveOnnboardingRequest(accountOwnerOnboardingRequest);

    }

    public TransactionResponseDto rejectOnnboardingRequest(AccountOwnerOnboardingRequest accountOwnerOnboardingRequest){
        return jsonClientWrapper.rejectOnnboardingRequest(accountOwnerOnboardingRequest);
    }


    public List<AccountOwnerOnboardingResponse> listAccountOwnerOnboardingRequest(String centralBankUser) throws Exception {
        return jsonClientWrapper.listAccountOwnerOnboardingRequest(centralBankUser);
    }

    public List<AccountOwnerOnboardingResponse> listAccountOwnerApprovedRequest(String centralBankUser) throws Exception {
        return jsonClientWrapper.listAccountOwnerApprovedRequest(centralBankUser);
    }


    public List<AccountOwnerOnboardingResponse> listAccountOwnerCancelledRequest(String centralBankUser) throws Exception {
        return jsonClientWrapper.listAccountOwnerCancelledRequest(centralBankUser);
    }

    public List<RtgsTransactionResponseProposalDto> listPendingWithdrawals(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/withdrawalRequests/",  TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listCancelledWithdrawals(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/cancelledWithdrawalRequests/",  TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listApprovedWithdrawals(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/approvedWithdrawalRequests/",  TransactionType.WITHDRAWAL, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listPendingDeposits(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/depositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listCancelledDeposits(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/cancelledDepositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listApprovedDeposits(String centralBankUser, String userName) throws Exception {
        return jsonClientWrapper.listRtgsTransactionRequestProposals(centralBankUser,"/api/search/approvedDepositRequests/", TransactionType.DEPOSIT, userName);
    }

    public List<RtgsTransactionResponseProposalDto> listApprovedRtgsTransactions(String centralBankUser, String userName) throws Exception {
        List<RtgsTransactionResponseProposalDto> listApprovedRtgsTransactions = new ArrayList<>();
        listApprovedRtgsTransactions.addAll( this.listApprovedDeposits(centralBankUser, userName));
        listApprovedRtgsTransactions.addAll( this.listApprovedWithdrawals(centralBankUser, userName));
        return listApprovedRtgsTransactions;
    }

    public List<RtgsTransactionResponseProposalDto> listPendingRtgsTransactions(String centralBankUser, String userName) throws Exception {
        List<RtgsTransactionResponseProposalDto> listPendingRtgsTransactions = new ArrayList<>();
        listPendingRtgsTransactions.addAll(this.listPendingDeposits(centralBankUser, userName));
        listPendingRtgsTransactions.addAll(this.listPendingWithdrawals(centralBankUser, userName));
        return listPendingRtgsTransactions;
    }

    public AccountOwnerDto getAccountOwner(String shortName) throws Exception {
        return  jsonClientWrapper.getAccountOwner(shortName);
    }

    public List<PvpIntegrationDto> listPvpTrades(String bankShortName) throws Exception{
        List<PvpIntegrationDto> pvps = jsonClientWrapper.listPvps(bankShortName, "/api/search/pvp/");

        return pvps;

    }

    public List<PvpIntegrationDto> listCancelledPvps(String bankShortName) throws Exception{
        return jsonClientWrapper.listCancelledPvps(bankShortName);
    }

    public List<PvpIntegrationDto> listCompletedPvps(String bankShortName) throws Exception{
        return jsonClientWrapper.listCompletedPvps(bankShortName);
    }

    public List<PvPProposal> listPvpProposals(String bankShortName) throws Exception{
            return jsonClientWrapper.listPvpProposals(bankShortName);
    }

    public String fundPvp(FundPvpDto fundPvpDto){
        return jsonClientWrapper.fundPvp(fundPvpDto);
    }

    public String acceptPvp(FundPvpDto fundPvpDto){
        return jsonClientWrapper.fundPvp(fundPvpDto);
    }

    public String rejectPvp(FundPvpDto fundPvpDto){
        return jsonClientWrapper.fundPvp(fundPvpDto);
    }



    public List<DvpIntegrationDto> listDvpTrades(String bankShortName) throws Exception{
        List<DvpIntegrationDto> dvps = jsonClientWrapper.listDvps(bankShortName, "/api/search/dvp/");
        return dvps;
    }

    public List<DvpIntegrationDto> listCancelledDvps(String bankShortName) throws Exception{
        return jsonClientWrapper.listCancelledDvps(bankShortName);
    }

    public List<DvpIntegrationDto> listCompletedDvps(String bankShortName) throws Exception{
        return jsonClientWrapper.listCompletedDvps(bankShortName);
    }

    public List<CSDAccountOwner> listCsdParticipantBanks(String centralBankUesr) throws Exception {
        return jsonClientWrapper.listCsdParticipantBanks(centralBankUesr);
    }

    public List<CSDAccount> listCsdParticipantBankAccountId(String bankShortName) throws Exception {
        return jsonClientWrapper.listCsdParticipantBankAccountId(bankShortName);
    }

    public String fundDvp(FundDvpDto fundDvpDto){
        return jsonClientWrapper.fundDvp(fundDvpDto);
    }

    public String acceptDvp(FundDvpDto fundPvpDto){
        return jsonClientWrapper.fundDvp(fundPvpDto);
    }

    public String rejectDvp(FundPvpDto fundPvpDto){
        return jsonClientWrapper.fundPvp(fundPvpDto);
    }



    public List<DomainResponseDto> listConnectedDomains() throws Exception {
        return jsonClientWrapper.listConnectedDomains();
    }

    public List<DomainResponseDto> listAllDomains() throws Exception {
        return jsonClientWrapper.listAllDomains();
    }
    public String connectDomains(DomainDto domainDto) {
          return jsonClientWrapper.connectDomains(domainDto);
    }

    public String disConnectDomains(DomainDto domainDto) {
        return jsonClientWrapper.disConnectDomains(domainDto);
    }

//-Dccbdc.operator=MAS
//-Dcdapps.service.host=localhost
//-Dcdapps.service.port=8081
//-Dcserver.port=4048
//docker run -p 8000:80 -e JAVA_TOOL_OPTIONS='-Dspring.profiles.active=dockerdev' demo-app

//docker  run --net host -e "JAVA_TOOL_OPTIONS=-Dcbdc.operator=MAS -Ddapps.service.host=127.0.0.1 -Ddapps.service.port=8081 -Dserver.port=8080" -p 8080:8080 g20-ui





}
