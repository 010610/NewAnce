
package newance.mzjava.mol.modification.unimod.jaxb;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for composition_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="composition_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="element" type="{http://www.unimod.org/xmlns/schema/unimod_2}elem_ref_t" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="composition" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mono_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="avge_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "composition_t", namespace = "http://www.unimod.org/xmlns/schema/unimod_2", propOrder = {
    "element"
})
@XmlSeeAlso({
    NeutralLossT.class,
    PepNeutralLossT.class
})
public class CompositionT {

    @XmlElement(namespace = "http://www.unimod.org/xmlns/schema/unimod_2")
    protected List<ElemRefT> element;
    @XmlAttribute(name = "composition", required = true)
    protected String composition;
    @XmlAttribute(name = "mono_mass")
    protected Double monoMass;
    @XmlAttribute(name = "avge_mass")
    protected Double avgeMass;

    /**
     * Gets the value of the element property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the element property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElemRefT }
     * 
     * 
     */
    public List<ElemRefT> getElement() {
        if (element == null) {
            element = new ArrayList<ElemRefT>();
        }
        return this.element;
    }

    /**
     * Gets the value of the composition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComposition() {
        return composition;
    }

    /**
     * Sets the value of the composition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComposition(String value) {
        this.composition = value;
    }

    /**
     * Gets the value of the monoMass property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMonoMass() {
        return monoMass;
    }

    /**
     * Sets the value of the monoMass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMonoMass(Double value) {
        this.monoMass = value;
    }

    /**
     * Gets the value of the avgeMass property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAvgeMass() {
        return avgeMass;
    }

    /**
     * Sets the value of the avgeMass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAvgeMass(Double value) {
        this.avgeMass = value;
    }

}
