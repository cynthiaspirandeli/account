package br.com.mobilecard.currentaccount;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mobilecard.currentaccount.model.ManualEntry;
import br.com.mobilecard.json.InternalServerException;
import br.com.mobilecard.services.rest.RestExceptionType;

@Path("currentaccount/manual_entry")
@Component
public class ManualEntryWS {

	@Autowired
	private ManualEntryBusiness manualEntryBusiness;

	@Path("/{storeDataId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@PathParam("storeDataId") Long storeDataId,
	        @QueryParam("forecastPaymentDate") String forecastPaymentDate, ManualEntry manualEntry) {
		if (storeDataId == null) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "storeDataId");
		}
		if (manualEntry == null) {
			throw new InternalServerException(RestExceptionType.NOT_NULL, "manualEntry");
		}
		try {
			manualEntryBusiness.save(manualEntry, storeDataId, DateTime.parse(forecastPaymentDate));
		} catch (IllegalArgumentException e) {
			throw new InternalServerException(RestExceptionType.INVALID_DATE, "forecastPaymentDate");
		}
	}

}
