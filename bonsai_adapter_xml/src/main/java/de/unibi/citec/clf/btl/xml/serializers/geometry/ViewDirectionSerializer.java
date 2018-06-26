
/*
 * ViewDirection.java
 * 
 * Copyright (C) 2010 Bielefeld University Copyright (C) 2010 Patrick Holthaus
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unibi.citec.clf.btl.xml.serializers.geometry;

import nu.xom.Attribute;
import nu.xom.Element;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.ViewDirection;



/**
 * Parameterized view angles for the control of a robot head or a pan/tilt
 * camera. <viewangle type="relative" reference="1224498541412"
 * purpose="interact" unit="degrees" pan="-2" tilt="10" inertia="strong" />
 * <viewangle type="absolute" reference="1224498541412" purpose="interact"
 * unit="real" x="1232.2" y="5123.1" z="34.2" inertia="strong" />
 * 
 * @author pholthau
 */
public class ViewDirectionSerializer extends XomSerializer<ViewDirection> {

	public static final String UNIT_ATTR_NAME = "unit";
	public static final String TYPE_ATTR_NAME = "type";
	public static final String PURPOSE_ATTR_NAME = "purpose";
	public static final String INERTIA_ATTR_NAME = "inertia";
	public static final String REF_ATTR_NAME = "reference";
	public static final String PAN_ATTR_NAME = "pan";
	public static final String TILT_ATTR_NAME = "tilt";

	public ViewDirectionSerializer(ViewDirection v) {
	}
	public ViewDirectionSerializer() {
    }

	@Override
	public void doFillInto(ViewDirection data, Element parent) throws SerializationException {
		final Element viewangle = parent;
		viewangle.addAttribute(new Attribute(UNIT_ATTR_NAME, data.unit.name()));
		viewangle.addAttribute(new Attribute(TYPE_ATTR_NAME, data.type.name()));
		viewangle.addAttribute(new Attribute(PURPOSE_ATTR_NAME, data.purpose
				.name()));
		viewangle.addAttribute(new Attribute(INERTIA_ATTR_NAME, data.inertia
				.name()));
		viewangle.addAttribute(new Attribute(REF_ATTR_NAME, String
				.valueOf(data.reference)));
		viewangle.addAttribute(new Attribute(PAN_ATTR_NAME, String
				.valueOf(data.pan)));
		viewangle.addAttribute(new Attribute(TILT_ATTR_NAME, String
				.valueOf(data.tilt)));

	}

	@Override
	public ViewDirection doFromElement(Element element) {

		ViewDirection vd = new ViewDirection();
		
		final ViewDirection.Type type = ViewDirection.Type.valueOf(element
				.getAttributeValue(TYPE_ATTR_NAME));
		final ViewDirection.Purpose purpose = ViewDirection.Purpose
				.valueOf(element.getAttributeValue(PURPOSE_ATTR_NAME));
		final ViewDirection.Unit unit = ViewDirection.Unit.valueOf(element
				.getAttributeValue(UNIT_ATTR_NAME));
		final ViewDirection.Inertia inertia = ViewDirection.Inertia
				.valueOf(element.getAttributeValue(INERTIA_ATTR_NAME));
		final float pan = Float.valueOf(element
				.getAttributeValue(PAN_ATTR_NAME));
		final float tilt = Float.valueOf(element
				.getAttributeValue(TILT_ATTR_NAME));
		final long reference = Long.valueOf(element
				.getAttributeValue(REF_ATTR_NAME));

		vd.type = type;
		vd.purpose = purpose;
		vd.unit = unit;
		vd.inertia = inertia;

		vd.pan = pan;
		vd.tilt = tilt;
		vd.reference = reference;

		return vd;
	}

	@Override
	public Class<ViewDirection> getDataType() {
		return ViewDirection.class;
	}

	@Override
	public String getBaseTag() {
		return "viewangle";
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public void doSanitizeElement(Element parent) {

	}

}
