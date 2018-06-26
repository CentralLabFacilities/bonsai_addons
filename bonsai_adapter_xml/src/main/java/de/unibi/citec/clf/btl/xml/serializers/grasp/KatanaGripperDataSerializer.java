package de.unibi.citec.clf.btl.xml.serializers.grasp;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.grasp.KatanaGripperData;
public class KatanaGripperDataSerializer extends XomSerializer<KatanaGripperData> {
	/**
	 * Constructor.
	 */
	public KatanaGripperDataSerializer() {
		super();
	}

	@Override
	public Class<KatanaGripperData> getDataType() {
		return KatanaGripperData.class;
	}

	@Override
	public String getBaseTag() {
		return "SENSORS";
	}

	@Override
	public KatanaGripperData doFromElement(Element element)
			throws ParsingException, DeserializationException {

		KatanaGripperData kgd = new KatanaGripperData();
		
		/*
		 * right finger
		 */
		kgd.setForceRightInsideNear(Double.parseDouble(element
				.query("FINGER[@position=\"right\"]/"
						+ "FORCE[@name=\"inside_near\"]/@value").get(0)
				.getValue()));
		kgd.setForceRightInsideFar(Double.parseDouble(element
				.query("FINGER[@position=\"right\"]/"
						+ "FORCE[@name=\"inside_far\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredRightOutside(Double.parseDouble(element
				.query("FINGER[@position=\"right\"]/"
						+ "INFRARED[@name=\"outside\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredRightFront(Double
				.parseDouble(element
						.query("FINGER[@position=\"right\"]/"
								+ "INFRARED[@name=\"front\"]/@value").get(0)
						.getValue()));
		kgd.setInfraredRightInsideNear(Double.parseDouble(element
				.query("FINGER[@position=\"right\"]/"
						+ "INFRARED[@name=\"inside_near\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredRightInsideFar(Double.parseDouble(element
				.query("FINGER[@position=\"right\"]/"
						+ "INFRARED[@name=\"inside_far\"]/@value").get(0)
				.getValue()));

		/*
		 * left finger
		 */
		kgd.setForceLeftInsideNear(Double.parseDouble(element
				.query("FINGER[@position=\"left\"]/"
						+ "FORCE[@name=\"inside_near\"]/@value").get(0)
				.getValue()));
		kgd.setForceLeftInsideFar(Double.parseDouble(element
				.query("FINGER[@position=\"left\"]/"
						+ "FORCE[@name=\"inside_far\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredLeftOutside(Double.parseDouble(element
				.query("FINGER[@position=\"left\"]/"
						+ "INFRARED[@name=\"outside\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredLeftFront(Double
				.parseDouble(element
						.query("FINGER[@position=\"left\"]/"
								+ "INFRARED[@name=\"front\"]/@value").get(0)
						.getValue()));
		kgd.setInfraredLeftInsideNear(Double.parseDouble(element
				.query("FINGER[@position=\"left\"]/"
						+ "INFRARED[@name=\"inside_near\"]/@value").get(0)
				.getValue()));
		kgd.setInfraredLeftInsideFar(Double.parseDouble(element
				.query("FINGER[@position=\"left\"]/"
						+ "INFRARED[@name=\"inside_far\"]/@value").get(0)
				.getValue()));

		/*
		 * middle
		 */
		kgd.setInfraredMiddle(Double.parseDouble(element
				.query("MIDDLE/INFRARED[@name=\"middle\"]/@value").get(0)
				.getValue()));
		return kgd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFillInto(KatanaGripperData data, Element parent) throws SerializationException {
		/*
		 * right finger
		 */
		Element rightFingerElement = new Element("FINGER");
		parent.appendChild(rightFingerElement);
		rightFingerElement.addAttribute(new Attribute("position", "right"));
		Element forceRightInsideNear = new Element("FORCE");
		rightFingerElement.appendChild(forceRightInsideNear);
		forceRightInsideNear.addAttribute(new Attribute("name", "inside_near"));
		forceRightInsideNear.addAttribute(new Attribute("value", String
				.valueOf(data.getForceRightInsideNear())));
		Element forceRightInsideFar = new Element("FORCE");
		rightFingerElement.appendChild(forceRightInsideFar);
		forceRightInsideFar.addAttribute(new Attribute("name", "inside_far"));
		forceRightInsideFar.addAttribute(new Attribute("value", String
				.valueOf(data.getForceRightInsideFar())));
		Element infraredRightOutside = new Element("INFRARED");
		rightFingerElement.appendChild(infraredRightOutside);
		infraredRightOutside.addAttribute(new Attribute("name", "outside"));
		infraredRightOutside.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredRightOutside())));
		Element infraredRightFront = new Element("INFRARED");
		rightFingerElement.appendChild(infraredRightFront);
		infraredRightFront.addAttribute(new Attribute("name", "front"));
		infraredRightFront.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredRightFront())));
		Element infraredRightInsideNear = new Element("INFRARED");
		rightFingerElement.appendChild(infraredRightInsideNear);
		infraredRightInsideNear.addAttribute(new Attribute("name",
				"inside_near"));
		infraredRightInsideNear.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredRightInsideNear())));
		Element infraredRightInsideFar = new Element("INFRARED");
		rightFingerElement.appendChild(infraredRightInsideFar);
		infraredRightInsideFar
				.addAttribute(new Attribute("name", "inside_far"));
		infraredRightInsideFar.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredRightInsideFar())));

		/*
		 * left finger
		 */
		Element leftFingerElement = new Element("FINGER");
		parent.appendChild(leftFingerElement);
		leftFingerElement.addAttribute(new Attribute("position", "left"));
		Element forceLeftInsideNear = new Element("FORCE");
		leftFingerElement.appendChild(forceLeftInsideNear);
		forceLeftInsideNear.addAttribute(new Attribute("name", "inside_near"));
		forceLeftInsideNear.addAttribute(new Attribute("value", String
				.valueOf(data.getForceLeftInsideNear())));
		Element forceLeftInsideFar = new Element("FORCE");
		leftFingerElement.appendChild(forceLeftInsideFar);
		forceLeftInsideFar.addAttribute(new Attribute("name", "inside_far"));
		forceLeftInsideFar.addAttribute(new Attribute("value", String
				.valueOf(data.getForceLeftInsideFar())));
		Element infraredLeftOutside = new Element("INFRARED");
		leftFingerElement.appendChild(infraredLeftOutside);
		infraredLeftOutside.addAttribute(new Attribute("name", "outside"));
		infraredLeftOutside.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredLeftOutside())));
		Element infraredLeftFront = new Element("INFRARED");
		leftFingerElement.appendChild(infraredLeftFront);
		infraredLeftFront.addAttribute(new Attribute("name", "front"));
		infraredLeftFront.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredLeftFront())));
		Element infraredLeftInsideNear = new Element("INFRARED");
		leftFingerElement.appendChild(infraredLeftInsideNear);
		infraredLeftInsideNear
				.addAttribute(new Attribute("name", "inside_near"));
		infraredLeftInsideNear.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredLeftInsideNear())));
		Element infraredLeftInsideFar = new Element("INFRARED");
		leftFingerElement.appendChild(infraredLeftInsideFar);
		infraredLeftInsideFar.addAttribute(new Attribute("name", "inside_far"));
		infraredLeftInsideFar.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredLeftInsideFar())));

		/*
		 * middle
		 */
		Element middleElement = new Element("MIDDLE");
		parent.appendChild(middleElement);
		Element middleInfrared = new Element("INFRARED");
		middleElement.appendChild(middleInfrared);
		middleInfrared.addAttribute(new Attribute("name", "middle"));
		middleInfrared.addAttribute(new Attribute("value", String
				.valueOf(data.getInfraredMiddle())));
		
	}

}
