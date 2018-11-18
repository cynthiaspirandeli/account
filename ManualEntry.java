package br.com.mobilecard.currentaccount.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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

import br.com.mobilecard.currentaccount.api.NonConformType;
import br.com.mobilecard.json.JodaDateTimeDeserializer;
import br.com.mobilecard.json.MoneyDeserializer;

@Entity
@Table(name = "T_MANUAL_ENTRY")
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD.UnusedPrivateField")
public class ManualEntry implements Serializable {

	private static final long serialVersionUID = 563039553876295689L;

	// Attributes
	public static final String ID = "id";

	@Id
	@SequenceGenerator(name = "PBatchGen", sequenceName = "S_PAYMENT_BATCH", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PBatchGen")
	@Column(name = "ID")
	private Long id;

	/**
	 * Data de criação do registro.
	 */
	@Column(name = "CREATED_DATE")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonDeserialize(using = JodaDateTimeDeserializer.class)
	private DateTime createdDate;

	/**
	 * Tipo de não conformidade.
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "ENTRY_TYPE")
	private int nonConformTypeCode;

	/**
	 * Descrição do motivo do lançamento manual
	 */
	@Column(name = "DESCRIPTION", length = 400)
	private String description;

	/**
	 * usuário que lançou manualmente entrada para o cliente
	 */
	@Column(name = "REGISTERED_BY")
	private String registeredBy;

	/**
	 * Valor do registro. Positivo se for algum credito para o estabelecimento e
	 * negativo se for algum debito.
	 */
	@Type(type = "org.autonomous.money.persistence.MoneyUserType")
	@Columns(columns = { @Column(name = "ENTRY_AMOUNT_CURRENCY", length = 3, nullable = false),
	        @Column(name = "ENTRY_AMOUNT_VALUE", precision = 10, scale = 2, nullable = false) })
	@JsonDeserialize(using = MoneyDeserializer.class)
	private Money entryValue;

	public NonConformType getNonConformType() {
		return NonConformType.getNonConformTypeById(nonConformTypeCode);
	}

	public void setNonConformType(NonConformType nonConformType) {
		this.nonConformTypeCode = nonConformType.getId();
	}

}
