package br.com.mobilecard.currentaccount.model;

import br.com.mobilecard.currentaccount.api.enums.FidelityOrigin;
import br.com.mobilecard.json.JodaDateTimeDeserializer;
import br.com.mobilecard.json.MoneyDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.autonomous.money.Money;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "T_CC_FIDELITY_REWARD")
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD.UnusedPrivateField")
public class FidelityReward implements Serializable {

	private static final long serialVersionUID = -6592534452761612788L;
	
	public static final String SALE_TRANSACTION_ID = "saleTransactionId";
	public static final String ORIGIN = "origin";
	
	@Id
	@SequenceGenerator(name = "TRecordGen", sequenceName = "S_CC_FIDELITY_REWARD", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRecordGen")
	@Column(name = "ID")
	private Long id;
	
	/**
	 * Sale that originated the fidelity reward
	 */
	@Column(name = "SALE_TRANSACTION_ID")
	private Long saleTransactionId;
	
	/**
	 * StoreDate of the sale
	 */
	@Column(name = "STORE_DATA_ID")
	private Long storeDataId;
	/**
	 * Trade association fidelity from
	 */
	@Column(name = "TRADE_ASSOCIATION_ID")
	private Long tradeAssociationId;
	
	
	@Column(name = "CREATED_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime createdDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ORIGIN")
	private FidelityOrigin origin;
	/**
	 * The fidelity valid from date (store can user it after this date)
	 */
	@Column(name = "VALID_FROM_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime validFromDate;
	
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "AMOUNT_CURRENCY", length = 3, nullable = false),
	        @Column(name = "AMOUNT_VALUE", precision = 10, scale = 2, nullable = false) })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money amount;
	
	@Column(name = "INSTALLMENT_NUMBER")
	private int installmentNumber;
	
	@Column(name = "INSTALLMENT_TOTAL_NUMBER")
	private int installmentTotalNumber;
	
	/**
	 * Pct of fidelity
	 */
	@Column(name = "FIDELITY_PCT")
	private BigDecimal fidelityPct;
	
	/**
	 * Expiration date: maximum date to redeem the fidelity reward
	 */
	@Column(name = "EXPIRATION_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime expirationDate;
	
	/**
	 * Redemption date: date when the store redeemed the fidelity reward
	 */
	@Column(name = "REDEMPTION_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime redemptionDate;
	
}
