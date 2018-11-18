package br.com.mobilecard.currentaccount.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AccessLevel;
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

import br.com.mobilecard.currentaccount.api.TransactionRecordStatus;
import br.com.mobilecard.currentaccount.api.TransactionRecordType;
import br.com.mobilecard.json.JodaDateTimeDeserializer;
import br.com.mobilecard.json.MoneyDeserializer;
import br.com.mobilecard.util.MoneyFormatUtils;

@Entity
@Table(name = "T_TRANSACTION_RECORD")
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD.UnusedPrivateField")
public class TransactionRecord implements Serializable {

	private static final long serialVersionUID = 935906635594873240L;

	// Attributes
	public static final String ID = "id";
	public static final String SALE_TRANSACTION_ID = "saleTransactionId";
	public static final String INSTALLMENT_NUMBER = "instalmentNumber";
	public static final String TRANSACTION_TYPE_CODE = "transactionRecordTypeCode";
	public static final String PAYMENT_SLIP_ID = "paymentSlipId";
	public static final String TRANSACTION_RECORD_STATUS = "transactionRecordStatus";
	public static final String PAYMENT_BLOCKED = "paymentBlocked";
	public static final String STORE_DATA = "storeData";
	public static final String DATE = "date";
	public static final String VALOR_LIQUIDO_DA_PARCELA = "valorLiquidoDaParcela";

	@Id
	@SequenceGenerator(name = "TRecordGen", sequenceName = "S_TRANSACTION_RECORD", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRecordGen")
	@Column(name = "ID")
	private Long id;

	/**
	 * Venda a que se refere esse registro, se for um registro relacionado a
	 * venda. Se for um outro tipo de registro, esse atributo fica nulo.
	 */
	@Column(name = "SALE_TRANSACTION_ID")
	private Long saleTransactionId;
	/**
	 * Boleto a que se refere esse registro, se for um registro relacionado a
	 * boleto. Se for um outro tipo de registro, esse atributo fica nulo.
	 */
	@Column(name = "PAYMENT_SLIP_ID")
	private Long paymentSlipId;
	/**
	 * Lançamento manual a que se refere esse registro. Se for um outro tipo de
	 * registro, esse atributo fica nulo.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MANUAL_ENTRY_ID")
	private ManualEntry manualEntry;
	/**
	 * Tipo de registro (venda, venda parcelada, boleto, mensalidade, lançamento
	 * manual, etc...).
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "TRANSACTION_RECORD_TYPE", nullable = false)
	private int transactionRecordTypeCode;
	/**
	 * Valor do registro. Positivo se for algum credito para o estabelecimento e
	 * negativo se for algum debito. Corresponde ao valor base de cálculo do
	 * registro. Portanto, se for uma venda parcelada, corresponde ao valor
	 * bruto da parcela. Se for uma transação não parcelada será igual ao valor
	 * bruto da transação. Nesse segundo caso (não ser uma venda parcelada),
	 * sempre vai ter o mesmo valor do atributo totalGrossAmount (valor bruto da
	 * transação). No caso de transação cujo valor do registro é superior ao
	 * valor original, esse campo contém o valor bruto que serve como base de
	 * cálculo para pagamento do lojista. Por exemplo, se um boleto foi pago com
	 * atraso, o pagamento inclui juros e multa. Portanto o valor pago é
	 * superior ao valor original do boleto. Neste caso, esse campo contém o
	 * valor final pago, não o valor original do boleto.
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "AMOUNT_CURRENCY", length = 3, nullable = false),
	        @Column(name = "AMOUNT_VALUE", precision = 10, scale = 2, nullable = false) })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money amount;
	/**
	 * A data quando o valor será creditado ou debitado para o estabelecimento.
	 * Ou seja, é a data prevista de pagamento. A data prevista de pagamento é
	 * diferente da data de pegamento. A data de pagamentoi se refere à data em
	 * que o pagamento foi efetivamente autorizado e enviado para o banco.
	 */
	@Column(name = "FORECAST_MERCHANT_PAYMENT_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime date;
	/**
	 * Numero da parcela a que se refere esse registro, quando se refere a uma
	 * venda parcelada. Se não for uma transação parcelada (boleto, mensalidade,
	 * etc.) conterá sempre o valor "1".
	 */
	@Column(name = "INSTALMENT_NUMBER")
	private int instalmentNumber;
	/**
	 * Lojista que criou a transação.
	 */
	@Column(name = "STORE_DATA_ID")
	private Long storeData;
	/**
	 * Data de criação do registro no sistema.
	 */
	@Column(name = "CREATED_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime createdDate;
	/**
	 * Valor bruto do registro. Positivo se for algum credito para o
	 * estabelecimento e negativo se for algum debito. Se for uma transacao
	 * parcelada, corresponde ao valor total da venda. Se for uma transacao a
	 * vista ou boleto ou qualquer outro tipo de transação não parcelada,
	 * correspponde ao valor da transacao. No caso de transação cujo valor do
	 * registro é superior ao valor original, esse campo contém o valor bruto
	 * que serve como base de cálculo para pagamento do lojista. Por exemplo, se
	 * um boleto foi pago com atraso, o pagamento inclui juros e multa. Portanto
	 * o valor pago é superior ao valor original do boleto. Neste caso, esse
	 * campo contém o valor final pago, não o valor original do boleto.
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "TOTAL_GROSS_AMOUNT_CURRENCY", length = 3, nullable = false),
	        @Column(name = "TOTAL_GROSS_AMOUNT_VALUE", precision = 10, scale = 2, nullable = false) })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money totalGrossAmount;
	/**
	 * Taxa de desconto cobrada pela processadora (Redecard, Cielo, Amex, ...).
	 * Não faz sentido para boletos ou para outras transações que não passam por
	 * uma adquirente (mensalidade, lançamentos manuais, etc). Nesse caso, o
	 * campo fica com valor 0.
	 */
	@Column(name = "ACQUIRER_FEE")
	private BigDecimal acquirerFee;
	
	/**
	 * Valor cobrado pelo gateway de pagamento. Não faz sentido para boletos ou
	 * para outras transações que não passam por um gateway(mensalidade,
	 * lançamentos manuais, etc). Nesse caso, o campo fica com valor 0.
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "GATEWAY_FIXED_SERV_TAX_CURR"), @Column(name = "GATEWAY_FIXED_SERV_TAX_VALUE") })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money gatewayServiceAmount;
	/**
	 * Taxa cobrada pela MobileCard.
	 */
	@Column(name = "mobilecard_service_fee")
	private BigDecimal mobilecardServiceFee;
	/**
	 * Valor fixo cobrado pela Mobilecard.
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "MOBILECARD_FIXED_SERVICE_CURR"),
	        @Column(name = "MOBILECARD_FIXED_SERVICE_TAX") })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money mobilecardServiceAmount;
	/**
	 * Numero de parcelas da trasnação. No caso de uma transação parcelada é o
	 * número de parcelas da transação. No caso de uma transação à vista ou
	 * boleto, ou qualquer outra transação não parcelada, esse valor é sempre
	 * '1'.
	 */
	@Column(name = "INSTALLMENTS_TOTAL_NUMBER")
	private int instalmentTotalNumber;
	/**
	 * Data em que o pagamento ao lojista foi enviado para o banco. Pode ser
	 * diferente da data prevista de pagamento, da data de aprovação do
	 * pagamento e da data de autorização de pagamento. Exemplificando: um
	 * registro pode ter data prevista de pagamento para 12/06/2013. Mas a
	 * aprovação do lote só foi realizada no dia 13/06/2013. Após a aprovação, o
	 * lote só foi efetivamente autorizado no dia 14/06/2013, mas depois do
	 * horário de execução do MobileBoy. Nesse caso, o pagamento será enviado
	 * para o banco no dia seguinte, ou seja, 15/06/2013. A data de aprovação e
	 * a data de autorização estão no lote de pagamento, não no registro da
	 * transação.
	 */
	@Column(name = "MERCHANT_PAYMENT_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime merchantPaymentDate;
	/**
	 * Data da confirmação de que o pagamento foi efetuado. No caso de pagamento
	 * manual, a data de confirmação é sempre igual à data de pagamento. No caso
	 * de pagamento automático (via MobileBoy), a confirmação normalmente ocorre
	 * no próximo dia útil após o pagamento. A confirmação é feita pelo
	 * MobileBoy ou pelo Sistema de Controle de Transações no momento da baixa
	 * de pagamento manual.
	 */
	@Column(name = "CONFIRM_MERCHANT_PAYMENT_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime merchantConfirmationPaymentDate;
	/**
	 * Valor liquido total da transacao. Se for uma transação parcelada,
	 * corresponde ao valor final liquido total da transação, ou seja, o valor
	 * liquido das parcelas somados. Este atributo não é usado para cálculo de
	 * pagamento. É usado somente com caráter demonstrativo em relatórios.
	 */
	@Column(name = "TOTAL_NET_VALUE")
	private BigDecimal valorLiquidoTotal;
	/**
	 * Valor líquido da parcela. É o valor efetivo a ser pago ao cliente. No
	 * caso de registro de transação não parceladas (transações à vista, boleto,
	 * mensalidades, etc), é igual ao valor líquido total.
	 */
	@Column(name = "INSTALLMENT_NET_VALUE")
	private BigDecimal valorLiquidoDaParcela;
	/**
	 * Valor líquido da taxa cobrada pela adquirente na parcela, ou seja, o
	 * valor que ela irá descontar do valor total da parcela. No caso de
	 * transações à vista o cálculo deste valor é baseado no montante total da
	 * transação. Não faz sentido para boletos ou para outras transações que não
	 * passam por uma adquirente (mensalidade, lançamentos manuais, etc). Nesse
	 * caso, o campo fica com valor 0.
	 */
	@Column(name = "INSTALLMENT_NET_VALUE_ACQUIRER")
	private BigDecimal valorLiquidoTaxaAdquirente;
	/**
	 * A data quando o valor será creditado ou debitado para a Mobilecard. Este
	 * campo so e preenchido se a transacao foi antecipada com a adquirente.
	 */
	@Column(name = "ANTECIPATION_PAYMENT_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime antecipationPaymentDate;
	/**
	 * Registro da antecipação com adquirente referente a esse registro, se
	 * houver.
	 */
	@Column(name = "ACQUIRER_ANTECIPATION_ID")
	private Long acquirerAntecipation;
	/**
	 * Data em que o pagamento da adquirente para a MobileCard sera efetuado.
	 * Este valor é preenchido quando o registro da transação é criado, sendo
	 * uma previsão de pagamento usada na conciliação. Não é a data de pagamento
	 * de antecipação (antecipationPaymentDate).
	 */
	@Column(name = "ACQUIRER_PAYMENT_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime acquirerPaymentDate;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "TRANSACTION_RECORD_STATUS")
	private TransactionRecordStatus transactionRecordStatus;
	/**
	 * Mensalidade a que se refere esse registro, se for um registro relacionado
	 * a mensalidade. Se for um outro tipo de registro, esse atributo fica nulo.
	 */
	@Column(name = "MONTHLY_PAYMENT_ID")
	private Long monthlyPayment;

	@Column(name = "PAYMENT_BLOCKED")
	private boolean paymentBlocked;

	/*
	 * Associação que irá receber o lançamento (rateio, cancelamento de venda = cancelamento do rateio,
	 * chargeback, etc)
	 */
	@Column(name = "TRADE_ASSOCIATION_ID")
	private Long tradeAssociationId;

	/*
	 * Pct do rateio. Pct calculado em cima do valor da venda pertecente à associação que cadastrou um lojista
	 */
	@Column(name = "SPLIT_PCT")
	private BigDecimal splitPct;

	/*
	 * Valor fixo cobrado por venda realizada de um lojista cadastro por uma associação
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "SPLIT_VALUE_CURRENCY"),
	        @Column(name = "SPLIT_VALUE") })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money splitFixedValue;

	/**
	 * Atributo calculado dinâmicamente para a tabela da página de antecipação
	 * com adquirente.
	 */
	@Transient
	private Double acquirerAntecipationValue;
	/**
	 * variável que armazena se o lançamento será ou não aprovado
	 */
	@Transient
	private boolean approved;
	/**
	 * Retorna o id externo da transação de acordo com o tipo da mesma
	 */
	@Transient
	private String transactionExternalId;

	/**
	 * representa o lote autorizador que o lançamento está relacionado na
	 * conciliação
	 */
	@Transient
	private Long authorizerBatch;

	/**
	 * representa o codigo autorizador que o lançamento está relacionado na
	 * conciliação
	 */
	@Transient
	private String authorizerCode;

	public String getAmountFormatted() {
		return MoneyFormatUtils.formatMoneyBrazil(amount.getValue());
	}

	public TransactionRecordType getTransactionRecordType() {
		return TransactionRecordType.getTransactionRecordTypeById(transactionRecordTypeCode);
	}

	public void setTransactionRecordType(TransactionRecordType transactionRecordType) {
		this.transactionRecordTypeCode = transactionRecordType.getId();
	}

	public String getTotalGrossAmountFormatted() {
		return MoneyFormatUtils.formatMoneyBrazil(totalGrossAmount.getValue());
	}

	public String getValorLiquidoDaParcelaFormatted() {
		return MoneyFormatUtils.formatMoneyBrazil(valorLiquidoDaParcela);
	}

	/**
	 * @return a string com o número da parcela/total de parcelas
	 */
	public String getInstalmentNumberFormated() {

		String instalmentNumberFormated = instalmentNumber + "/";

		if (instalmentNumber == 0) {
			instalmentNumberFormated = "1/";
		}

		instalmentNumberFormated += instalmentTotalNumber;

		return instalmentNumberFormated;
	}

	/**
	 * @return a data formatada sem horas, minutos ou segundos
	 */
	public String getAcquirerPaymentDateFormated() {
		if (acquirerPaymentDate == null) {
			return null;
		}
		return acquirerPaymentDate.toString("dd/MM/yyyy");
	}

	public boolean isApproved() {
		return approved;
	}

	/**
	 * @return a data formatada sem horas, minutos ou segundos
	 */
	public String getMerchantPaymentDateFormated() {
		return date.toString("dd/MM/yyyy");
	}

	public String getSaleDescription() {
		return TransactionRecordType.getAsString(this.getTransactionRecordType().getId());
	}

}
