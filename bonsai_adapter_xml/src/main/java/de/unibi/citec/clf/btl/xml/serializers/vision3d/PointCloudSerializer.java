package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.vision3d.PointCloud;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point3DSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Rotation3DSerializer;

/**
 * This is a serializable pointcloud type.
 * 
 * @author plueckin
 */
public class PointCloudSerializer extends XomSerializer<PointCloud> {

    public static String SENSORORIGIN_TAG = "SENSOR_ORIGIN";

    private Point3DSerializer pSerializer = new Point3DSerializer();
    private Rotation3DSerializer rSerializer = new Rotation3DSerializer();

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     * 
     * @return xml base tag
     */
    @Override
    public Class<PointCloud> getDataType() {
        return PointCloud.class;
    }

    @Override
    public String getBaseTag() {
        return "POINTCLOUD";
    }

    /**
     * Constructs a {@link PointCloudSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to construct an object from.
     * @return The {@link PointCloudSerializer} object containing all the
     *         information given by the {@link Element} object.
     */
    @Override
    public PointCloud doFromElement(Element element) throws ParsingException, DeserializationException {
        PointCloud type = new PointCloud();
        try {

            Elements points = element
                    .getChildElements(pSerializer.getBaseTag());
            for (int i = 0; i < points.size(); i++) {
                Element point = points.get(i);
                type.addPoint(pSerializer.fromElement(point));
            }

            Element srot = element.getFirstChildElement(rSerializer
                    .getBaseTag());

            Element sori = element.getFirstChildElement(SENSORORIGIN_TAG);
            double spx = Double.parseDouble(sori.getAttribute("x").getValue());
            double spy = Double.parseDouble(sori.getAttribute("y").getValue());
            double spz = Double.parseDouble(sori.getAttribute("z").getValue());

            type.setSensorOrigin(new Point3D(spx, spy, spz, PointCloud.iLU));
            type.setRotation(rSerializer.fromElement(srot));

        } catch (NullPointerException ex) {

            // this happens when an element or attribute that is required is
            // not present
            throw new ParsingException("Missing element or attribute "
                    + "in document.", ex);
        } catch (NumberFormatException e) {
            throw new ParsingException("could not parse coordinate values");
        }
        return type;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    /**
     * Serializes the {@link PointCloudSerializer} object into a given XOM
     * {@link Element} .
     * 
     * @param parent
     *            The {@link Element} to serialize the object into. The given
     *            {@link Element} object should have the base tag defined by
     *            this class. (see {@link #getClass().getSimpleName()})
     * @see #getClass().getSimpleName()
     */
    @Override
    public void doFillInto(PointCloud data, Element parent) throws SerializationException {

        Elements oldPoints = parent.getChildElements(pSerializer.getBaseTag());
        for (int i = 0; i < oldPoints.size(); i++) {
            parent.removeChild(oldPoints.get(i));
        }

        for (Point3D p : data.getPoints()) {
            Element item = new Element(pSerializer.getBaseTag());
            pSerializer.fillInto(p, item);
            parent.appendChild(item);
        }

        Elements oldRot = parent.getChildElements(rSerializer.getBaseTag());
        for (int i = 0; i < oldRot.size(); i++) {
            parent.removeChild(oldRot.get(i));
        }

        Element rot = new Element(rSerializer.getBaseTag());
        rSerializer.fillInto(data.getRotation(), rot);
        parent.appendChild(rot);

        Elements oldSensorOrig = parent.getChildElements(SENSORORIGIN_TAG);
        for (int i = 0; i < oldSensorOrig.size(); i++) {
            parent.removeChild(oldSensorOrig.get(i));
        }
        Element sensororig = new Element(SENSORORIGIN_TAG);
        sensororig.addAttribute(new Attribute("x", String.valueOf(data
                .getSensorOrigin().getX(PointCloud.iLU))));
        sensororig.addAttribute(new Attribute("y", String.valueOf(data
                .getSensorOrigin().getY(PointCloud.iLU))));
        sensororig.addAttribute(new Attribute("z", String.valueOf(data
                .getSensorOrigin().getZ(PointCloud.iLU))));
        parent.appendChild(sensororig);

    }
}
