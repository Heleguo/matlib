package me.matl114.matlib.algorithms.algorithm;

import lombok.NoArgsConstructor;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.Debug;
import org.bukkit.util.Transformation;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformationUtils {
    public static final Quaternionf R = new Quaternionf(0,0,0,1);
    public static final Quaternionf I = new Quaternionf(1,0,0,0);
    public static final Quaternionf J = new Quaternionf(0,1,0,0);
    public static final Quaternionf K = new Quaternionf(0,0,1,0);
    public static final Quaternionf ZERO = new Quaternionf(0,0,0,0);
    public static final Vector3f ID_SCALE = new Vector3f(1,1,1);
    public static final Vector3f ZERO_VEC = new Vector3f(0,0,0);
    public static final Vector3f X = new Vector3f(1,0,0);
    public static final Vector3f Y = new Vector3f(0,1,0);
    public static final Vector3f Z = new Vector3f(0,0,1);
    private static final Transformation DEFAULT_TRANSFORMATION = new Transformation(ZERO_VEC,R,ID_SCALE,R);
    private static final Matrix4f Id4 = new Matrix4f().identity();
    public static Vector3f q2v(Quaternionf q) {
        return new Vector3f(q.x,q.y,q.z);
    }
    public static Quaternionf v2q(Vector3f v) {
        return new Quaternionf(v.x,v.y,v.z,0);
    }
    public static Vector3f cloneVec(Vector3f v) {
        try{
            return (Vector3f) v.clone();
        }catch (CloneNotSupportedException exception){
            throw new RuntimeException(exception);
        }
    }
    static{

    }
    public static Quaternionf cloneQ(Quaternionf q) {
        try{
            return (Quaternionf) q.clone();
        }catch (CloneNotSupportedException exception){
            throw new RuntimeException(exception);
        }
    }
    public static Vector3f mulElem(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.x*v2.x,v1.y*v2.y,v1.z*v2.z);
    }

    /**
     * from axis angle to quaternion, using
     * @param axis
     * @param angle
     * @return
     */
    public static Quaternionf fromAxisAngle(Vector3f axis, float angle) {
        if(axis.x==0 && axis.y==0 && axis.z==0){
            return R;
        }
        Vector3f normedAxis = cloneVec(axis).normalize();
        angle = angle * 0.5f;
        float sin= Math.sin(angle);
        return new Quaternionf(normedAxis.x*sin,normedAxis.y*sin,normedAxis.z*sin,Math.cosFromSin(sin, angle));
    }

    /**
     * from vector dot, we assume that angle greater than 0 and  less than PI
     * @param x
     * @param y
     * @param z
     * @param cosValue
     * @return
     */
    @Note("thetaValue of cosValue should be less than PI and greater than 0,or you should use -x -y -z")
    private static Quaternionf axisAngleWithCos(float x, float y, float z, float cosValue, boolean isSinValuePositve){
        //IMPORTANT
        //cos2θ=2cosθ^2 -1
        float halfAngleCos = Math.sqrt(0.5f*(cosValue + 1));
        float sin = Math.sqrt(1.0f - halfAngleCos* halfAngleCos) * (isSinValuePositve?1:-1);
        return new Quaternionf(x*sin,y*sin,z*sin,halfAngleCos);
    }
    public static float angleDegreeToRadian(float degree) {
        return Math.toRadians(degree);
    }
    /**
      随便写点
     scale 和 右旋 是用来确定展示实体的形状的
     我们在进行变换合成的时候可以忽略真的吗
     假设我们尝试改变了scale(x,x,x) （x=x=x)
     不对 scale可以直接塞到q里
     他会给|q|^2的缩放
     那么我可以提出来x 直接作用到translatiion就可以了
     那么现在问题就是
     固定的旋转 怎么运算
     首先我们可以知道 p'(Ax')p'^-1 + t2 = p'(A(pxp^-1 +t1))p'^-1 +t2
     那么合成运算将会是p'p AX (p'p)^-1 +p'At1p'^-1 +t2
     */



    public static Transformation defaultTrans() {
        return DEFAULT_TRANSFORMATION;
    }


    /**
     * 线性保角变换
     * 只包括旋转,平移,集体缩放
     * 我们这里设置为Acx+b A是旋转 c是缩放倍率 b是平移
     * 在四元数形式下为 q(cx)q^-1 +b b be w=0的四元数
     */

    @Note("LC means Linear Conformal, it is a composition of rotation, scale and translation")
    public static record LCTransformation(@Note("q should satisfy ||q||=1") Quaternionf q, float c, Quaternionf b)  {
        /**
         * q should be ||q||=1
         * f.compositionWith(g) means f(g(x))
         * q1(c1(q2(c2x)q2^-1 +b2))q1^-1 +b1
         * q1q2(c1c2(x))(q1q2)^-1 +q1c1b2q1^-1 +b1
         * @param other
         * @return
         */
        @Note("f.compose(g) means f·g ,which f·g(x) = f(g(x))")
        public LCTransformation compositionWith(LCTransformation other) {
            Quaternionf newQ =  cloneQ(q).mul(other.q);
            float newC = c*other.c;
            Quaternionf newB = cloneQ(q).mul(c).mul(other.b).mul(cloneQ(q).invert()).add(b);
            return new LCTransformation(newQ,newC,newB);
        }
        public static LCTransformation ofTransformation(Transformation transformation) {
            return new LCTransformation(cloneQ(transformation.getLeftRotation()),1.0f,v2q(transformation.getTranslation()));
        }
        public Transformation transformOrigin(Transformation origin) {
            LCTransformation tran = this.compositionWith( ofTransformation(origin));

            return new Transformation(q2v(tran.b),cloneQ(tran.q),cloneVec(origin.getScale()).mul(tran.c),cloneQ(origin.getRightRotation()));
        }
        private static final LCTransformation Id = new LCTransformation(R,1.0f,ZERO);
        public static LCTransformation ofIdentical() {
            return Id;
        }
        public LCTransformation noBias(){
            return new LCTransformation(cloneQ(q),c,ZERO);
        }
        public Vector3f bias(){
            return q2v(b);
        }
    }

    public static class LinearTransFactory{
        Quaternionf q = R;
        Vector3f b = ZERO_VEC;
        float c = 1.0f;
        public LinearTransFactory C(float c){
            this.c = c;
            return this;
        }
        public LinearTransFactory A(Quaternionf q){
            this.q = q;
            return this;
        }
        public LinearTransFactory A(float x, float y, float z,float delta){
            this.q = fromAxisAngle(new Vector3f(x,y,z),delta);
            return this;
        }
        public LinearTransFactory A(float x, float y, float z,int degree){
            this.q = fromAxisAngle(new Vector3f(x,y,z),Math.toRadians(degree));
            return this;
        }
        public LinearTransFactory A(Vector3f v ,float delta ){
            this.q = fromAxisAngle(v,delta);
            return this;
        }
        public LinearTransFactory bias(Vector3f b){
            this.b = b;
            return this;
        }
        public LCTransformation build(){
            return new LCTransformation(q,c,v2q(b));
        }
    }
    @NoArgsConstructor
    public static class TransFactory {
        Quaternionf q1 = R;
        Vector3f s = ID_SCALE;
        Quaternionf q2 = R;
        Vector3f d = ZERO_VEC;

        public TransFactory preRotation(float x, float y, float z ,float angle) {
            this.q1 = fromAxisAngle(new Vector3f(x,y,z),angle);
            return this;
        }
        public TransFactory preRotation(Quaternionf q) {
            this.q1 = q;
            return this;
        }
        public TransFactory scale(float x, float y, float z) {
            this.s = new Vector3f(x,y,z);
            return this;
        }
        public TransFactory postRotation(float x, float y, float z ,int angleDegree) {
            return postRotation(x,y,z,Math.toRadians(angleDegree));
        }
        public TransFactory postRotation(float x, float y, float z ,float angle) {
            this.q2 = fromAxisAngle(new Vector3f(x,y,z),angle);
            return this;
        }
        public TransFactory postRotation(Quaternionf q) {
            this.q2 = q;
            return this;
        }
        public TransFactory translate(float x, float y, float z) {
            this.d = new Vector3f(x,y,z);
            return this;
        }
        public TransFactory addTranslate(float x, float y, float z){
            this.d = this.d == null ? new Vector3f(x,y,z): cloneVec(d).add(x,y,z);
            return this;
        }
        public Transformation build(){

            return new Transformation(cloneVec(d),cloneQ(q2),cloneVec(s),cloneQ(q1));
        }

        public TransFactory copy() {
            var re = new TransFactory();
            re.q1 = q1;
            re.s = s;
            re.q2 = q2;
            re.d = d;
            return re;
        }
    }
    public static TransFactory builder(){
        return new TransFactory();
    }


    public static LinearTransFactory linearBuilder(){
        return new LinearTransFactory();
    }
    public static LCTransformation linearBias(Vector3f bias){
        return new LCTransformation(R,1.0f,v2q(bias));
    }
    public static LCTransformation linearAxisAngle(Vector3f axis,float angle){
        return new LCTransformation(fromAxisAngle(axis,angle),1.0f,ZERO);
    }



    public static Transformation shrink(Transformation trans, Vector3f vec){
        return new Transformation(trans.getTranslation(),trans.getLeftRotation(), mulElem(vec,trans.getScale()),trans.getRightRotation() );
    }
    public static Transformation rotation(Quaternionf f){
        return new Transformation(ZERO_VEC,f,ID_SCALE,R);
    }
    public static LCTransformation rotationAsLinear(Quaternionf f){
        return new LCTransformation(f,1.0f,ZERO);
    }

    /**
     * create a block with bottom and top parallel to xz plane ,and with relative-(0,0,0)->(dx,dy,dz) translation ,and with Shape (shrinkX,shrinkY,shrinkZ),and with rotation degree from default rotation
     * @param dx
     * @param dy
     * @param dz
     * @param shrinkX
     * @param shrinkY
     * @param shrinkZ
     * @param degree
     * @return
     */
    public static TransFactory buildFlatBlockAt(float dx, float dy, float dz,float shrinkX,float shrinkY,float shrinkZ,float degree){
        return builder()
                .scale(shrinkX,shrinkY,shrinkZ)
                .postRotation(0,1,0,degree)
                .translate(dx,dy,dz)
                ;
    }

    /**
     * flat block with rotation 0(origin place)
     * @param dx
     * @param dy
     * @param dz
     * @param shrinkX
     * @param shrinkY
     * @param shrinkZ
     * @return
     */
    public static TransFactory buildCubeAt(float dx, float dy, float dz,float shrinkX,float shrinkY,float shrinkZ){
        return buildFlatBlockAt(dx,dy,dz,shrinkX,shrinkY,shrinkZ,0);
    }
    public static TransFactory buildCubeAtCenter(float shapeX,float shapeY,float shapeZ){
        return buildCubeAt(-shapeX/2.0f,-shapeY/2.0f,-shapeZ/2.0f,shapeX,shapeY,shapeZ);
    }

    /**
     * this method create a rotation Quaternion which rotate directed line <(0,0,0),(0,1,0)> to <(0,0,0),(vec)>
     * shouldRotateXZ determines whether rotate XZ to make XY plane-> X vec plane
     */
    public static Quaternionf rotateOriginTo(Vector3f vec,boolean shouldRotateXZ){
        //首先我们求出他的旋转轴
        float thetaCos = vec.y/vec.length();
        float invlen = Math.invsqrt( vec.x*vec.x+vec.z*vec.z);
        Vector3f rotationVec = new Vector3f(vec.z * invlen,0,-vec.x* invlen).normalize();
        //y should be 0,xz
        Quaternionf rotate =  axisAngleWithCos(rotationVec.x,0,rotationVec.z,thetaCos,true);
        if(shouldRotateXZ){
            //
            Debug.logger(rotate);
            //右手旋转,按y+旋转的话需要旋转-theta度数
            Quaternionf rotateXZ = axisAngleWithCos(0,-1,0, vec.x*invlen , (vec.z>0));
            Debug.logger(rotateXZ);
            rotate = rotate.mul(rotateXZ);
            Debug.logger(rotate);
        }
        return rotate;
    }
    //todo need a rotate from Direction to Direction
}
