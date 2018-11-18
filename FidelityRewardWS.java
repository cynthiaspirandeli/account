package br.com.mobilecard.currentaccount;

import br.com.mobilecard.currentaccount.api.enums.FidelityOrigin;
import br.com.mobilecard.currentaccount.model.FidelityReward;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("currentaccount/fidelity_reward")
@Component
public class FidelityRewardWS {

	@Autowired
	private FidelityRewardBusiness fidelityRewardBusiness;

	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveOrUpdateTransactionRecords(
			List<FidelityReward> fidelityRewards) {
		fidelityRewardBusiness.saveOrUpdateFidelityRewards(fidelityRewards);
	}

	@Path("/{saleTransactionId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FidelityReward> findBySaleIdAndOrigin(
			@PathParam("saleTransactionId") Long saleTransactionId,
			@QueryParam("origin") String origin) {
		return fidelityRewardBusiness.findBySaleIdAndOrigin(saleTransactionId, FidelityOrigin.valueOf(origin));

	}
}
