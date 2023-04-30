/* Copyright (c) 2009-2023 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jMe3GL2.scene.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.io.IOException;

/**
 * Un objeto de la clase <code>Sprite</code> es un malla que se encarga de
 * manejar 'modelos' 2D.
 * <p>
 * Con un <code>Sprite</code> puede voltear una imagen horizontalmente, así 
 * como verticalmente o una combinación de ambas.</p>
 * 
 * <p>
 * Esta malla utiliza una escala de <code>0.0-1.0</code> que se puede traducir 
 * como 0% y 100%, ten encuenta esta escala al cargar la textura de un modelo 2D.
 * </p>
 * 
 * <p>
 * <b>Ejemplo:</b>
 * <pre><code>
 * Si una textura tiene los siguiente datos:
 * 
 * Anchura: 100px;
 * Largo  :  50px;
 * 
 * Tenemos que tomar una medida como referencia al 100% de las dimensiones para
 * poder definir y así evitar deformar la textura de la geometría.
 * 
 * En este caso tomaremos la anchura, con este dato referenciado las dimensiones
 * de esta malla serían:
 * 
 * width:  1.0F; // Equivalente al 100%.
 * height: 0.5F; // Equivalente al 50%
 * 
 * Este análisis equivale a decir que la anchura es el 100%, mientras que el largo
 * es quivalente al 50% de la anchura. Si se hubiera tomado los datos de manera
 * inversa, seria que ahora la anchura es el 50% del largo que es el 100%.
 * </code></pre>
 * </p>
 * 
 * @author wil
 * @version 1.5-SNAPSHOT
 * 
 * @since 1.0.1
 */
public class Sprite extends Mesh implements Cloneable {
    
    /**
     * Objeto encargado de gestionar la transformación de la malla, es decir
     * sus vérticess así como las coordenadas de la textura de esta malla.
     */
    private Transform transform;
    
    /**
     * {@code true} si se desea voltear la textura de la malla en la horizontal,
     * de lo contrario {@code false} si se desea regresar la textura a su estado
     * original.
     */
    private boolean flipH;
    
    /**
     * {@code true} si se desea voltear la textura de la malla en la vertical,
     * de lo contrario {@code false} si se desea regresar la textura a su estado
     * original.
     */
    private boolean flipV;
    
    /**
     * Solo serialización. No utilice.
     */
    public Sprite() {
    }

    /**
     * Instancia un nuevo objeto <code>Sprite</code>. Establezca las 
     * dimensiones que tendrá los vértices de la malla.
     * 
     * @param width el ancho deseado.
     * @param height la altura deseada.
     */
    public Sprite(float width, float height) {
        this(width, height, 1, 1, 0, 0);
    }

    /**
     * Instancia un nuevo objeto <code>Sprite</code>. Establezca los valores
     * predeterminados de la malla.
     * 
     * @param width el ancho deseado.
     * @param height la altura deseada.
     * @param columns número de columnas deseada. 
     * @param rows número de filas deseada.
     * @param colPosition posición de columna.
     * @param rowPosition posición de fila.
     */
    public Sprite(float width, float height, int columns, int rows, int colPosition, int rowPosition) {
        this.transform = new Transform(width, height, columns, rows, colPosition, rowPosition);
        this.initializeMesh();
    }

    /**
     * Método encargado de inicializar y/o configurar la malla.
     */
    private void initializeMesh() {
        transform.setFlipType(Transform.FlipType.NonFlip);
        
        // Índices. Definimos el orden en que se debe construir la malla
        short[] indexes = {2, 0, 1, 1, 3, 2};

        // Configuración de búferes
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(transform.getTextureCoordinates()));
        setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1});
        setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        updateBound();
    }
    
    /**
     * (non-JavaDoc)
     * @see Mesh#jmeClone() 
     * @return Clons
     */
    @Override
    public Sprite jmeClone() {
        return (Sprite) super.jmeClone();
    }

    /**
     * (non-JavaDoc)
     * @see Mesh#deepClone() 
     * @return Clons
     */
    @Override
    public Sprite deepClone() {
        Sprite clon = (Sprite) super.deepClone();
        clon.transform = transform.clone();
        clon.flipH = flipH;
        clon.flipV = flipV;
        return clon;
    }

    /**
     * (non-JavaDoc)
     * @see Mesh#clone() 
     * @return Clons
     */
    @Override
    public Sprite clone() {
        Sprite clon = (Sprite) super.clone();
        clon.transform = transform.clone();
        clon.flipH = flipH;
        clon.flipV = flipV;
        return clon;
    }
    
    /**
     * Actualiza el tamaño de esta malla.
     * @param width ancho-malla.
     * @param height largo-malla.
     */
    public void updateVertexSize(float width, float height) {
        transform.setSize(width, height);
        
        // Configuración de búferes
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
        updateTextureCoords();
        updateBound();
    }

    
    /**
     * Actualiza las coordenadas de la textura.
     * <p>
     * Esto se puede utilizar cuando se haga un cambio en el {@code Transform}
     * de esta malla.</p>
     */
    public void updateTextureCoords() {
        updateTextureCoords(transform.getColPosition(), transform.getRowPosition());
    }
    
    /**
     * Actualiza las coordenadas de la textura con nuevas posiciones.
     * @param colPosition Nueva posición de columna.
     * @param rowPosition Nueva posición de fila.
     */
    public void updateTextureCoords(int colPosition, int rowPosition) {
        transform.setPosition(colPosition, rowPosition);
        
        if ( flipH && flipV ) {
            transform.setFlipType(Transform.FlipType.Flip_HV);
        } else if ( flipH ) {
            transform.setFlipType(Transform.FlipType.Flip_H);
        } else if ( flipV ) {
            transform.setFlipType(Transform.FlipType.Flip_V);
        } else {
            transform.setFlipType(Transform.FlipType.NonFlip);
        }

        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(transform.getTextureCoordinates()));
    }

    /**
     * Establece el tipo del volteado en la horizontal.
     * @param flipH {@code true} o {@code false} para voltear.
     */
    public void flipH(boolean flipH) {
        if (this.flipH != flipH) {
            this.flipH = flipH;
            this.updateTextureCoords();
        }
    }

    /**
     * Establece el tipo del volteado en la vertical.
     * @param flipV {@code true} o {@code false} para voltear.
     */
    public void flipV(boolean flipV) {
        if (this.flipV != flipV) {
            this.flipV = flipV;
            this.updateTextureCoords();
        }
    }
    
    /**
     * Método encargado de escalar las dimensiones de esta malla.
     * @param scale nueva escala.
     */
    public void scale(float scale) {
        this.scale(scale, scale);
    }
    
    /**
     * Método encargado de escalar las dimensiones de esta malla.
     * @param scaleX escala en el eje {@code x}.
     * @param scaleY escala en el eje {@code y}.
     */
    public void scale(float scaleX, float scaleY) {
        final Vector2f newVs = new Vector2f(scaleX, scaleY);
        if (this.transform.getScale()
                .equals(newVs)) {
            return;
        }
        
        this.transform.setScale(newVs);
        
        // Se establece nuevas vertices para la malla.
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
        
        // Actualizamos las coordenadas.
        updateTextureCoords();
        updateBound();
    }

    /**
     * Devuelve el tipo de volteado en la horizontal.
     * @return Tipo de volteado.
     */
    public boolean isFlipH() {
        return flipH;
    }

    /**
     * Devuelve el tipo de volteado en la vertical.
     * @return Tipo de volteado.
     */
    public boolean isFlipV() {
        return flipV;
    }
    
    /**
     * Método encargado de distorsionar la textura de esta malla a través 
     * de un índice.
     * @param index Nuevo índice de actualización.
     */
    public void showIndex(int index) {
        updateTextureCoords(index % transform.getColumns(), index / transform.getColumns());
    }
    
    /**
     * Método encargado de distorsionar la textura de esta malla a través 
     * de un índice.
     * 
     * @param colPosition Nueva posición de columna.
     * @param rowPosition Nueva posición de fila.
     */
    public void showIndex(int colPosition, int rowPosition) {
        updateTextureCoords(colPosition, rowPosition);
    }

    /**
     * Devuelve el tranformador de esta malla.
     * @return Transformador.
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * (non-JavaDoc).
     * @see Transform#getWidth() 
     * @return Float.
     */
    public float getWidth() {
        return transform.getWidth();
    }

    /**
     * (non-JavaDoc).
     * @see Transform#getHeight() 
     * @return Float.
     */
    public float getHeight() {
        return transform.getHeight();
    }

    /**
     * (non-JavaDoc).
     * @see Transform#getScale() 
     * @return Vector.
     */
    public Vector2f getScale() {
        return transform.getScale();
    }

    /**
     * (non-JavaDoc).
     * 
     * @param im JmeImporter
     * @see Mesh#read(com.jme3.export.JmeImporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        transform = (Transform) in.readSavable("transform", new Transform());
        flipH = in.readBoolean("flipH", Boolean.FALSE);
        flipV = in.readBoolean("flipV", Boolean.FALSE);
        initializeMesh();
    }

    /**
     * (non-JavaDoc).
     * 
     * @param ex JmeExporter.
     * @see Mesh#write(com.jme3.export.JmeExporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);        
        OutputCapsule out = ex.getCapsule(this);
        out.write(transform, "transform", null);
        out.write(flipH, "flipH", Boolean.FALSE);
        out.write(flipV, "flipV", Boolean.FALSE);
    }
}
