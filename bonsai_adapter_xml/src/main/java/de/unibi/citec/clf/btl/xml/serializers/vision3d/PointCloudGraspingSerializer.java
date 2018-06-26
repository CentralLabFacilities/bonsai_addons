package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.vision3d.PointCloudGrasping;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point3DSerializer;

/**
 * This is the representation of an Point Cloud with grasping point and label.
 * 
 * @author plueckin
 */

public class PointCloudGraspingSerializer extends XomSerializer<PointCloudGrasping> {

    private Point3DSerializer grasppnt = new Point3DSerializer();
    private PointCloudSerializer convhull = new PointCloudSerializer();

    public PointCloudGraspingSerializer() {
    }

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     * 
     * @return xml base tag
     */
    @Override
    public Class<PointCloudGrasping> getDataType() {
        return PointCloudGrasping.class;
    }

    @Override
    public String getBaseTag() {
        return "POINTCLOUDGRASPING";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        try {
            if (!(obj instanceof PointCloudGrasping))
                return false;

            PointCloudGrasping other = (PointCloudGrasping) obj;

            return other.equals(this);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void doFillInto(PointCloudGrasping data, Element parent) throws SerializationException {

        PointCloudSerializer pcs = new PointCloudSerializer();

        // cleanup
        Elements elements = parent.getChildElements(PointCloudGrasping.LABEL_TAG);
        for (int i = 0; i < elements.size(); i++) {
            parent.removeChild(elements.get(i));
        }
        elements = parent.getChildElements(PointCloudGrasping.GRASPPNT_TAG);
        for (int i = 0; i < elements.size(); i++) {
            parent.removeChild(elements.get(i));
        }
        elements = parent.getChildElements(pcs.getBaseTag());
        for (int i = 0; i < elements.size(); i++) {
            parent.removeChild(elements.get(i));
        }

        Element labelName = new Element(PointCloudGrasping.LABEL_TAG);
        labelName.addAttribute(new Attribute("value", data.getLabel()));
        parent.appendChild(labelName);

        Element grasp = new Element(PointCloudGrasping.GRASPPNT_TAG);
        grasppnt.fillInto(data.getGraspPoint(), grasp);
        parent.appendChild(grasp);

        Element convh = new Element(pcs.getBaseTag());
        convhull.fillInto(data.getConvexhull(), convh);
        parent.appendChild(convh);

        Elements oldHeight = parent.getChildElements(PointCloudGrasping.HEIGHT_TAG);
        for (int i = 0; i < oldHeight.size(); i++) {
            parent.removeChild(oldHeight.get(i));
        }
        Element height = new Element(PointCloudGrasping.HEIGHT_TAG);
        height.addAttribute(new Attribute("valh", String.valueOf(data.getHeight(PointCloudGrasping.iLU))));
        parent.appendChild(height);

        Elements oldWidth = parent.getChildElements(PointCloudGrasping.WIDTH_TAG);
        for (int i = 0; i < oldWidth.size(); i++) {
            parent.removeChild(oldWidth.get(i));
        }
        Element width = new Element(PointCloudGrasping.WIDTH_TAG);
        width.addAttribute(new Attribute("valw", String.valueOf(data.getWidth(PointCloudGrasping.iLU))));
        parent.appendChild(width);

        Elements oldDepth = parent.getChildElements(PointCloudGrasping.DEPTH_TAG);
        for (int i = 0; i < oldDepth.size(); i++) {
            parent.removeChild(oldDepth.get(i));
        }
        Element depth = new Element(PointCloudGrasping.DEPTH_TAG);
        depth.addAttribute(new Attribute("vald", String.valueOf(data.getDepth(PointCloudGrasping.iLU))));
        parent.appendChild(depth);

    }

    @Override
    public PointCloudGrasping doFromElement(Element element) throws ParsingException, DeserializationException {

        PointCloudGrasping pcg = new PointCloudGrasping();

        try {

            Element grasppnt = element.getFirstChildElement(PointCloudGrasping.GRASPPNT_TAG);
            double gpx = Double.parseDouble(grasppnt.getAttribute("x").getValue());
            double gpy = Double.parseDouble(grasppnt.getAttribute("y").getValue());
            double gpz = Double.parseDouble(grasppnt.getAttribute("z").getValue());

            Element height = element.getFirstChildElement(PointCloudGrasping.HEIGHT_TAG);
            double h = Double.parseDouble(height.getAttribute("valh").getValue());
            Element width = element.getFirstChildElement(PointCloudGrasping.WIDTH_TAG);
            double w = Double.parseDouble(width.getAttribute("valw").getValue());
            Element depth = element.getFirstChildElement(PointCloudGrasping.DEPTH_TAG);
            double d = Double.parseDouble(depth.getAttribute("vald").getValue());

            PointCloudSerializer pc = new PointCloudSerializer();
            Element convhull = element.getFirstChildElement(pc.getBaseTag());

            Element name = element.getFirstChildElement(PointCloudGrasping.LABEL_TAG);
            final String nameLabel = name.getAttribute("value").getValue();
            pcg.setLabel(nameLabel);
            pcg.setGraspPoint(gpx, gpy, gpz);
            pcg.setConvexhull(pc.fromElement(convhull));
            pcg.setHeight(h, PointCloudGrasping.iLU);
            pcg.setWidth(w, PointCloudGrasping.iLU);
            pcg.setDepth(d, PointCloudGrasping.iLU);
        } catch (NullPointerException ex) {

            // this happens when an element or attribute that is required is
            // not present
            throw new ParsingException("Missing element or attribute " + "in document.", ex);
        } catch (NumberFormatException e) {
            throw new ParsingException("could not parse coordinate values");
        }
        return pcg;
    }

    @Override
    public void doSanitizeElement(Element parent) {

    }
}
