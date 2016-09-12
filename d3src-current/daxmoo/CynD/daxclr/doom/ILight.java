package daxclr.doom;

public interface ILight extends IEntity {
    /**
     * lights
     *
     *
     *
     * Sets the shader to be used for the light.
     *
     * @param shader
     */
    public void setShader(String shader);

    /**
     * Gets a shader parameter.
     *
     * @param parmNum
     *
     * @return
     */
    public float getLightParm(float parmNum);

    /**
     * Sets a shader parameter.
     *
     * @param parmNum
     * @param value
     */
    public void setLightParm(float parmNum, float value);

    /**
     * Sets the red/green/blue/alpha shader parms on the light and the model.
     *
     * @param parm0
     * @param parm1
     * @param parm2
     * @param parm3
     */
    public void setLightParms(float parm0, float parm1, float parm2,
                              float parm3);

    /**
     * Sets the width/length/height of the light bounding box.
     *
     * @param x
     * @param y
     * @param z
     */
    public void setRadiusXYZ(float x, float y, float z);

    /**
     * Sets the size of the bounding box.
     *
     * @param radius
     */
    public void setRadius(float radius);

    /**
     * Turns the light on.
     */
    public void On();

    /**
     * Turns the light off.
     */
    public void Off();

    /**
     * Turns the light out over the given time in seconds.
     *
     * @param time
     */
    public void fadeOutLight(float time);

    /**
     * Turns the light on over the given time in seconds.
     *
     * @param time
     */
    public void fadeInLight(float time);

}
