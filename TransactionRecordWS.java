package br.com.mobilecard.currentaccount;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mobilecard.currentaccount.api.TransactionRecordStatus;
import br.com.mobilecard.currentaccount.model.TransactionRecord;
import br.com.mobilecard.json.InternalServerException;
import br.com.mobilecard.json.Json;
import br.com.mobilecard.services.rest.RestExceptionType;

import java.util.ArrayList;

import org.autonomous.money.Money;

@Path("currentaccount/transactions")
@Component
public class TransactionRecordWS {

	@Autowired
	private TransactionRecordBusiness transactionRecordBusiness;

	@Path("/{transactionRecordId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Json(exclude = "manualEntry")
	public TransactionRecord findById(
			@PathParam("transactionRecordId") Long transactionRecordId) {
		return transactionRecordBusiness.findById(transactionRecordId);
	}

	/**
	 * Updates the transaction records status, if updating to PAID the payment
	 * date should be passed.
	 *
	 * @param transactionRecords
	 * @param status
	 * @param paymentDate - optional (format ddMMyyyy)
	 */
	@Path("/updateStatus")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void updateTransactionRecordsStatus(List<Long> transactionRecords,
			@QueryParam("status") TransactionRecordStatus status,
			@DefaultValue("") @QueryParam("paymentDate") String paymentDate) {
		DateTime date = null;
		if (StringUtils.isNotEmpty(paymentDate)) {
			date = DateTime.parse(paymentDate,
					DateTimeFormat.forPattern("ddMMyyyy"));
		}
		transactionRecordBusiness.updateTransactionRecordsStatus(
				transactionRecords, status, date);
	}

	@Path("/findAllBySaleId/{saleTransactionId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TransactionRecord> findAllBySaleId(
			@PathParam("saleTransactionId") Long saleTransactionId) {
		return transactionRecordBusiness.findAllBySaleId(saleTransactionId);
	}

	@Path("/findAllIncludingCancelledByTransactionId/{transactionId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TransactionRecord> findAllIncludingCancelledByTransactionId(
			@PathParam("transactionId") Long transactionId) {
		return transactionRecordBusiness
				.findAllIncludingCancelledByTransactionId(transactionId);
	}

	@Path("/findAllByIdList")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Json(exclude = "manualEntry")
	public List<TransactionRecord> findAllByIdList(@QueryParam("transactionIds") List<Long> transactionIds) {
		return transactionRecordBusiness.findAllByIdList(transactionIds);
	}

	@Path("/by_id_list_with_pagination")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Json(exclude = "manualEntry")
	public List<TransactionRecord> findByIdListWithPagination(@QueryParam("transactionIds") String transactionIds,
			@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize) {

		if (StringUtils.isBlank(transactionIds)) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "transactionIds");
		}
		if (page == null) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "page");
		}
		if (pageSize == null) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "pageSize");
		}

		//the query param is splitted by comma
		List<Long> ids = new ArrayList<>();
		String[] idsArray = transactionIds.split("\\s*,\\s*");
		for (String idString : idsArray) {
			ids.add(Long.parseLong(idString));
		}
		return transactionRecordBusiness.findByIdListWithPagination(ids, page, pageSize);
	}

	@Path("/findAllFirstInstallmentIdsBySaleIds")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Long> findAllFirstInstallmentIdsBySaleIds(
			List<Long> saleTransactionIds) {
		return transactionRecordBusiness
				.findAllFirstInstallmentIdsBySaleIds(saleTransactionIds);
	}

	@Path("/findAllByIdListAndInstallmentNumber")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TransactionRecord> findAllByIdListAndInstallmentNumber(
			@QueryParam("transactionIds") List<Long> transactionIds,
			@QueryParam("installmentNumber") int installmentNumber) {
		return transactionRecordBusiness.findAllByIdListAndInstallmentNumber(
				transactionIds, installmentNumber);
	}

	@Path("/payment_slip/ids")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public List<Long> findAllPaymentSlipIdsByTransactionIds(
			List<Long> transactionIds) {
		return transactionRecordBusiness
				.findAllPaymentSlipIdsByTransactionIds(transactionIds);
	}

	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<TransactionRecord> saveOrUpdateTransactionRecords(
			List<TransactionRecord> transactionRecords) {
		transactionRecordBusiness.saveOrUpdateTransactionRecords(transactionRecords);
		return transactionRecords;
	}

	@Path("/{transactionId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTransactionRecord(@PathParam("transactionId") Long transactionId,
			TransactionRecord transactionRecord) {
		transactionRecordBusiness.updateTransactionRecord(transactionRecord);
	}

	@Path("/sales/{saleTransactionId}/block_payment")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void blockSaleTransactionsPayment(@PathParam("saleTransactionId") Long saleTransactionId) {
		transactionRecordBusiness.blockSaleTransactionsPayment(saleTransactionId);
	}

	@Path("/sales/{saleTransactionId}/unblock_payment")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void unblockSaleTransactionsPayment(@PathParam("saleTransactionId") Long saleTransactionId) {
		transactionRecordBusiness.unblockSaleTransactionsPayment(saleTransactionId);
	}

	@Path("/findTotalValueToReceive")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Money findTotalValueToReceiveInDateInterval(
			@QueryParam("storeDataId") Long storeDataId,
			@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		return transactionRecordBusiness.findTotalValueToReceiveInDateInterval(storeDataId, startDate, endDate);
	}

	@Path("/find_transacions_to_pay_today_by_store_data_id")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Json(exclude = "manualEntry")
	public List<TransactionRecord> findTransactionsToPayTodayByStoreData(@QueryParam("storeDataId") Long storeDataId) {
		if (storeDataId == null) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "storeDataId");
		}
		return transactionRecordBusiness.findTransactionsToPayToday(storeDataId);
	}
	
	/**
	 * 
	 * @param transactionsRejected the ids of the transactions to not be returned by this method
	 * @param storeDataIds the ids of the storeDatas related to the transactions being approved
	 * @return 
	 */
	@Path("/find_approved_transacions_to_pay_today")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Json(exclude = "manualEntry")
	public List<TransactionRecord> findAllApprovedTransactionsToPayToday(List<Long> transactionsRejected, 
			@QueryParam("storeDataIds") String storeDataIds) {
		//the query param is splitted by comma
		String[] ids = storeDataIds.split("\\s*,\\s*");
		List<Long> idList = new ArrayList<>();
		for (String id : ids) {
			idList.add(Long.valueOf(id));
		}
		return transactionRecordBusiness.findAllApprovedTransactionsToPayToday(transactionsRejected, idList);
	}
}
