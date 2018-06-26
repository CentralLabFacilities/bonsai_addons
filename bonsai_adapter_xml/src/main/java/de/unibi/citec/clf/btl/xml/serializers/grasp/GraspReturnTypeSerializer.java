package de.unibi.citec.clf.btl.xml.serializers.grasp;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType.GraspResult;
import de.unibi.citec.clf.btl.units.LengthUnit;

/**
 * This class represents a point in space by all 3 dimensions.
 * 
 * @author lziegler
 */
public class GraspReturnTypeSerializer extends XomSerializer<GraspReturnType> {

	/**
	 * Creates instance.
	 */
	public GraspReturnTypeSerializer() {
	}

	@Override
	public Class<GraspReturnType> getDataType() {
		return GraspReturnType.class;
	}

	@Override
	public String getBaseTag() {
		return "GRASPRETURNTYPE";
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof GraspReturnTypeSerializer))
				return false;

			GraspReturnTypeSerializer other = (GraspReturnTypeSerializer) obj;

			return other.equals(this);

		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public GraspReturnType doFromElement(Element element)
			throws ParsingException, DeserializationException {
		
		GraspReturnType grt = new GraspReturnType();
		
		try {

			double x0 = Double.parseDouble(element.getAttribute("x")
					.getValue());
			double y0 = Double.parseDouble(element.getAttribute("y")
					.getValue());
			double z0 = Double.parseDouble(element.getAttribute("z")
					.getValue());
			double rating0 = Double.parseDouble(element.getAttribute("rating")
					.getValue());

			GraspResult gr0;
			try {
				gr0 = GraspResult.valueOf(element.getAttribute("graspResult")
						.getValue());
			} catch (IllegalArgumentException | NullPointerException e) {
				gr0 = GraspResult.NO_RESULT;
			}

            grt.setGraspResult(gr0);
			grt.setX(x0, LengthUnit.MILLIMETER);
			grt.setY(y0, LengthUnit.MILLIMETER);
			grt.setZ(z0, LengthUnit.MILLIMETER);
			grt.setRating(rating0);

		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present

			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse coordinate values.", e);
		}
		return grt;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFillInto(GraspReturnType data, Element parent) throws SerializationException {
		parent.addAttribute(new Attribute("x", String
				.valueOf(data.getX(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute("y", String
				.valueOf(data.getY(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute("z", String
				.valueOf(data.getZ(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute("rating", String
				.valueOf(data.getRating())));
		parent.addAttribute(new Attribute("graspResult", String.valueOf(data.getGraspResult())));
		
	}
}
