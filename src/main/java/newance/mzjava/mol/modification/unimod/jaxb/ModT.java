
package newance.mzjava.mol.modification.unimod.jaxb;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for mod_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mod_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specificity" type="{http://www.unimod.org/xmlns/schema/unimod_2}specificity_t" maxOccurs="unbounded"/>
 *         &lt;element name="delta" type="{http://www.unimod.org/xmlns/schema/unimod_2}composition_t"/>
 *         &lt;element name="Ignore" type="{http://www.unimod.org/xmlns/schema/unimod_2}composition_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alt_name" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="xref" type="{http://www.unimod.org/xmlns/schema/unimod_2}xref_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="misc_notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="title" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="full_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="username_of_poster" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group_of_poster" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="date_time_posted" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="date_time_modified" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="approved" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="ex_code_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="record_id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mod_t", namespace = "http://www.unimod.org/xmlns/schema/unimod_2", propOrder = {
    "specificity",
    "delta",
    "ignore",
    "altName",
    "xref",
    "miscNotes"
})
public class ModT {

    @XmlElement(namespace = "http://www.unimod.org/xmlns/schema/unimod_2", required = true)
    protected List<SpecificityT> specificity;
    @XmlElement(namespace = "http://www.unimod.org/xmlns/schema/unimod_2", required = true)
    protected CompositionT delta;
    @XmlElement(name = "Ignore", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<CompositionT> ignore;
    @XmlElement(name = "alt_name", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<String> altName;
    @XmlElement(namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<XrefT> xref;
    @XmlElement(name = "misc_notes", namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected String miscNotes;
    @XmlAttribute(name = "title", required = true)
    protected String title;
    @XmlAttribute(name = "full_name", required = true)
    protected String fullName;
    @XmlAttribute(name = "username_of_poster", required = true)
    protected String usernameOfPoster;
    @XmlAttribute(name = "group_of_poster")
    protected String groupOfPoster;
    @XmlAttribute(name = "date_time_posted", required = true)
    protected String dateTimePosted;
    @XmlAttribute(name = "date_time_modified", required = true)
    protected String dateTimeModified;
    @XmlAttribute(name = "approved")
    protected Boolean approved;
    @XmlAttribute(name = "ex_code_name")
    protected String exCodeName;
    @XmlAttribute(name = "record_id")
    protected Long recordId;

    /**
     * Gets the value of the specificity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specificity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecificity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecificityT }
     * 
     * 
     */
    public List<SpecificityT> getSpecificity() {
        if (specificity == null) {
            specificity = new ArrayList<SpecificityT>();
        }
        return this.specificity;
    }

    /**
     * Gets the value of the delta property.
     * 
     * @return
     *     possible object is
     *     {@link CompositionT }
     *     
     */
    public CompositionT getDelta() {
        return delta;
    }

    /**
     * Sets the value of the delta property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositionT }
     *     
     */
    public void setDelta(CompositionT value) {
        this.delta = value;
    }

    /**
     * Gets the value of the ignore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ignore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIgnore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositionT }
     * 
     * 
     */
    public List<CompositionT> getIgnore() {
        if (ignore == null) {
            ignore = new ArrayList<CompositionT>();
        }
        return this.ignore;
    }

    /**
     * Gets the value of the altName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAltName() {
        if (altName == null) {
            altName = new ArrayList<String>();
        }
        return this.altName;
    }

    /**
     * Gets the value of the xref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XrefT }
     * 
     * 
     */
    public List<XrefT> getXref() {
        if (xref == null) {
            xref = new ArrayList<XrefT>();
        }
        return this.xref;
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
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the usernameOfPoster property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsernameOfPoster() {
        return usernameOfPoster;
    }

    /**
     * Sets the value of the usernameOfPoster property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsernameOfPoster(String value) {
        this.usernameOfPoster = value;
    }

    /**
     * Gets the value of the groupOfPoster property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupOfPoster() {
        return groupOfPoster;
    }

    /**
     * Sets the value of the groupOfPoster property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupOfPoster(String value) {
        this.groupOfPoster = value;
    }

    /**
     * Gets the value of the dateTimePosted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateTimePosted() {
        return dateTimePosted;
    }

    /**
     * Sets the value of the dateTimePosted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateTimePosted(String value) {
        this.dateTimePosted = value;
    }

    /**
     * Gets the value of the dateTimeModified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateTimeModified() {
        return dateTimeModified;
    }

    /**
     * Sets the value of the dateTimeModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateTimeModified(String value) {
        this.dateTimeModified = value;
    }

    /**
     * Gets the value of the approved property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApproved() {
        return approved;
    }

    /**
     * Sets the value of the approved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApproved(Boolean value) {
        this.approved = value;
    }

    /**
     * Gets the value of the exCodeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExCodeName() {
        return exCodeName;
    }

    /**
     * Sets the value of the exCodeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExCodeName(String value) {
        this.exCodeName = value;
    }

    /**
     * Gets the value of the recordId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRecordId() {
        return recordId;
    }

    /**
     * Sets the value of the recordId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRecordId(Long value) {
        this.recordId = value;
    }

}
