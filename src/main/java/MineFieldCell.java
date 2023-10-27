/*
 * Class that represents (i.e., models) a cell in the mine field. A cell consists
 * of a contentType and an iconType. The former keeps the real value in the cell
 * while the latter keeps the displayed value/view of the cell as icon.
 */
public class MineFieldCell {
    private MinefieldModel.ContentType contentType;
    private MinefieldModel.IconType iconType;

    /*
     * The no-argument constructor sets the cell content to empty and covers it
     */
    public MineFieldCell(){
        setContentType(MinefieldModel.ContentType.ZERO);
        setIconType(MinefieldModel.IconType.TILE_COVERED);
    }

    /*
     * The constructor that takes in the content type and icon type
     */
    public MineFieldCell(MinefieldModel.ContentType contentType, MinefieldModel.IconType iconType){
        setContentType(contentType);
        setIconType(iconType);
    }
    /*
    * The getter for contentType
    */
    public MinefieldModel.ContentType getContentType() {
        return contentType;
    }
    /*
    * The setter for contentType
    */
    public void setContentType(MinefieldModel.ContentType contentType) {
        this.contentType = contentType;
    }
    /*
    * The getter for iconType
    */
    public MinefieldModel.IconType getIconType() {
        return iconType;
    }
    /*
    * The setter for iconType
    */
    public void setIconType(MinefieldModel.IconType iconType) {
        this.iconType = iconType;
    }
}
