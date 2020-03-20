
package newance.mzjava.mol.modification.unimod.jaxb;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for specificity_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="specificity_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NeutralLoss" type="{http://www.unimod.org/xmlns/schema/unimod_2}NeutralLoss_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PepNeutralLoss" type="{http://www.unimod.org/xmlns/schema/unimod_2}PepNeutralLoss_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="misc_notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hidden" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="site" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="position" use="required" type="{http://www.unimod.org/xmlns/schema/unimod_2}position_t" />
 *       &lt;attribute name="classification" use="required" type="{http://www.unimod.org/xmlns/schema/unimod_2}classification_t" />
 *       &lt;attribute name="spec_group" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "specificity_t", namespace = "http://www.unimod.org/xmlns/schema/unimod_2", propOrder = {
    "neutralLoss",
    "pepNeutralLoss",
    "miscNotes"
})
public class SpecificityT {

    @XmlElement(name = "NeutralLoss", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<NeutralLossT> neutralLoss;
    @XmlElement(name = "PepNeutralLoss", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<PepNeutralLossT> pepNeutralLoss;
    @XmlElement(name = "misc_notes", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected String miscNotes;
    @XmlAttribute(name = "hidden")
    protected Boolean hidden;
    @XmlAttribute(name = "site", required = true)
    protected String site;
    @XmlAttribute(name = "position", required = true)
    protected PositionT position;
    @XmlAttribute(name = "classification", required = true)
    protected String classification;
    @XmlAttribute(name = "spec_group")
    protected int specGroup;

    /**
     * Gets the value of the neutralLoss property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the neutralLoss property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNeutralLoss().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NeutralLossT }
     * 
     * 
     */
    public List<NeutralLossT> getNeutralLoss() {
        if (neutralLoss == null) {
            neutralLoss = new ArrayList<NeutralLossT>();
        }
        return this.neutralLoss;
    }

    /**
     * Gets the value of the pepNeutralLoss property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pepNeutralLoss property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPepNeutralLoss().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PepNeutralLossT }
     * 
     * 
     */
    public List<PepNeutralLossT> getPepNeutralLoss() {
        if (pepNeutralLoss == null) {
            pepNeutralLoss = new ArrayList<PepNeutralLossT>();
        }
        return this.pepNeutralLoss;
    }

    /**
     * Gets the value of the miscNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiscNotes() {
        return miscNotes;
    }

    /**
     * Sets the value of the miscNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiscNotes(String value) {
        this.miscNotes = value;
    }

    /**
     * Gets the value of the hidden property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHidden() {
        if (hidden == null) {
            return false;
        } else {
            return hidden;
        }
    }

    /**
     * Sets the value of the hidden property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHidden(Boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the value of the site property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSite() {
        return site;
    }

    /**
     * Sets the value of the site property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSite(String value) {
        this.site = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionT }
     *     
     */
    public PositionT getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionT }
     *     
     */
    public void setPosition(PositionT value) {
        this.position = value;
    }

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassification(String value) {
        this.classification = value;
    }

    /**
     * Gets the value of the specGroup property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public int getSpecGroup() {

        return specGroup;
    }

    /**
     * Sets the value of the specGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSpecGroup(int value) {
        this.specGroup = value;
    }

}
