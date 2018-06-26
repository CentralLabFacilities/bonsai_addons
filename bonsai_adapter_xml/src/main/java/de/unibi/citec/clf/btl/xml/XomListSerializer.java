package de.unibi.citec.clf.btl.xml;



import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * 
 * 
 * @author lziegler
 */
public abstract class XomListSerializer<T extends Type, L extends List<T>> extends XomSerializer<L> {

    public abstract XomSerializer<T> getItemSerializer();

    public abstract L getDefaultInstance();

    private XomSerializer<T> itemSerializer;

    public XomListSerializer() {
        itemSerializer = getItemSerializer();
    }

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     * 
     * @return xml base tag
     */
    @Override
    public String getBaseTag() {
        return itemSerializer.getBaseTag() + "LIST";
    }

    /**
     * Fills an {@link List} object from a given XOM {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to fill an object from.
     * @return {@link DoorListSerializer} object filled with all the information
     *         given by the {@link Element} object.
     * @throws ParsingException
     * @throws DeserializationException 
     */
    @Override
    public L doFromElement(Element objectElement) throws ParsingException, DeserializationException {
        L type = getDefaultInstance();
        try {
            // check hypotheses
            Nodes nodes = objectElement.query(itemSerializer.getBaseTag());
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node instanceof Element) {

                    Document doc = new Document(((Element) node.copy()));
                    T data = itemSerializer.deserialize(doc);
                    type.add(data);
                }
            }

        } catch (NullPointerException ex) {
            // this happens when an element or attribute that is required is
            // not present
            throw new IllegalArgumentException("Missing element or attribute " + "in document.", ex);
        }
        return type;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SerializationException
     */
    @Override
    public void doFillInto(L type, Element parent) throws SerializationException {

        for (T data : type) {
            Document doc = itemSerializer.serialize(data);
            Element dataElem = doc.getRootElement();
            parent.appendChild(dataElem.copy());
        }
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<L> getDataType() {
        return (Class<L>) getDefaultInstance().getClass();
    }
}
