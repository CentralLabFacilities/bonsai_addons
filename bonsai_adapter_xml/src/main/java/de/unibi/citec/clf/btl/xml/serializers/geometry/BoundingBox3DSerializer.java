package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;

public class BoundingBox3DSerializer extends XomSerializer<BoundingBox3D> {

	public static final String POSE_TAG_NAME = "POSE";
	public static final String SIZE_TAG_NAME = "SIZE";

	private static final Logger logger = Logger
			.getLogger(BoundingBox3DSerializer.class);

	@Override
	public Class<BoundingBox3D> getDataType() {
		return BoundingBox3D.class;
	}
	
	/**
	 * Creates a new instance.
	 */
	public BoundingBox3DSerializer() {
		super();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public String getBaseTag() {
		return "BOUNDINGBOX3D";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFillInto(BoundingBox3D data, Element parent) throws SerializationException {

		// cleanup
		Elements elements = parent.getChildElements(POSE_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		elements = parent.getChildElements(SIZE_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}

		// fill
		Pose3DSerializer p = new Pose3DSerializer();
		Element originElement0 = new Element(POSE_TAG_NAME);
		Element originElement1 = new Element(p.getBaseTag());
		p.fillInto(data.getPose(), originElement1);
		originElement0.appendChild(originElement1);
		parent.appendChild(originElement0);

		Point3DSerializer s = new Point3DSerializer();
		Element sizeElement = new Element(SIZE_TAG_NAME);
		Element sizeElement1 = new Element(s.getBaseTag());
		s.fillInto(data.getSize(), sizeElement1);
		sizeElement.appendChild(sizeElement1);
		parent.appendChild(sizeElement);
	}

	@Override
	public BoundingBox3D doFromElement(Element element)
			throws ParsingException, DeserializationException {
		
		BoundingBox3D bb = new BoundingBox3D();
		try {
			Pose3DSerializer pose = new Pose3DSerializer();
			Element originElement = element.getFirstChildElement(POSE_TAG_NAME);
			Element originElement1 = originElement.getFirstChildElement(pose.getBaseTag());
			
			bb.setPose(pose.fromElement(originElement1));

			Point3DSerializer size = new Point3DSerializer();
			Element sizeElement = element.getFirstChildElement(SIZE_TAG_NAME);
			Element sizeElement1 = sizeElement.getFirstChildElement(size.getBaseTag());
			
			bb.setSize(size.fromElement(sizeElement1));

		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			logger.error(e);
			throw new ParsingException(e.getMessage(), e);
		}
		return bb;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoundingBox3DSerializer) {
			BoundingBox3DSerializer other = (BoundingBox3DSerializer) obj;
			return super.equals(other);
		}
		return false;
	}

	@Override
	public void doSanitizeElement(Element parent) {

	}
}
