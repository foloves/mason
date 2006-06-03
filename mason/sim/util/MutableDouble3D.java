/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.util;

/** 
    MutableDouble3D is more or less the same class as javax.vecmath.Point3d, except that it is hash-equivalent to Double3D.  
        
    <p>Just as with MutableInt3D: you use MutableDouble3D as a STORED hash key at your peril: it has the same misfeature as 
    javax.vecmath.Point3d, and you should read the warning in Double3D.  However, you can look up Double3D-keyed 
    objects in a hash table by passing in a MutableDouble3D instead.    
*/

public final class MutableDouble3D implements java.io.Serializable, Cloneable
    {
    public double x;
    public double y;
    public double z;

    public MutableDouble3D() { x = 0.0; y = 0.0; z = 0.0;}
    /** Explicitly assumes the z value is set to 0 */
    public MutableDouble3D(final Int2D p) { x = p.x; y = p.y; z = 0.0; }
    public MutableDouble3D(final Int2D p, final double z) { x = p.x; y = p.y; this.z = z; }
    public MutableDouble3D(final Int3D p) { x = p.x; y = p.y ; z = p.z; }
    /** Explicitly assumes the z value is set to 0 */
    public MutableDouble3D(final MutableInt2D p) { x = p.x; y = p.y; z = 0.0; }
    public MutableDouble3D(final MutableInt2D p, final double z) { x = p.x; y = p.y; this.z = z; }
    public MutableDouble3D(final MutableInt3D p) { x = p.x; y = p.y ; z = p.z; }
    /** Explicitly assumes the z value is set to 0 */
    public MutableDouble3D(final Double2D p) { x = p.x; y = p.y; z = 0.0; }
    public MutableDouble3D(final Double2D p, final double z) { x = p.x; y = p.y; this.z = z; }
    public MutableDouble3D(final Double3D p) { x=p.x; y=p.y; z=p.z; }
    /** Explicitly assumes the z value is set to 0 */
    public MutableDouble3D(final MutableDouble2D p) { x = p.x; y = p.y; z = 0.0; }
    public MutableDouble3D(final MutableDouble2D p, final double z) { x = p.x; y = p.y; this.z = z; }
    public MutableDouble3D(final MutableDouble3D p) { x=p.x; y=p.y; z=p.z; }
    public MutableDouble3D(final double x, final double y, double z) { this.x = x; this.y = y; this.z = z;}
    public final double getX() { return x; }
    public final double getY() { return y; }
    public final double getZ() { return z; }
    public final void setX(double val) { x = val; }
    public final void setY(double val) { y = val; }
    public final void setZ(double val) { z = val; }
    public void setTo(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    public void setTo(Int3D p) { x = p.x; y = p.y; z = p.z; }
    public void setTo(MutableInt3D p) { x = p.x; y = p.y; z = p.z; }
    public void setTo(Double3D p) { x = p.x; y = p.y; z = p.z; }
    public void setTo(MutableDouble3D p) { x = p.x; y = p.y; z = p.z; }
    public String toString() { return "MutableDouble3D["+x+","+y+","+z+"]"; }
    public String toCoordinates() { return "(" + x + ", " + y + ", " + z + ")"; }
    public Object clone()
        { 
        try 
            { 
            return super.clone(); 
            }
        catch(CloneNotSupportedException e)
            { 
            return null; // never happens
            } 
        }

    public int hashCode()
        {
        // so we hash to the same value as Int2D does, if we're ints
        if ((((int)x) == x) && (((int)y) == y) && (((int)z) == z))
            //  return Int3D.hashCodeFor((int)x,(int)y,(int)z);
        
            {
            int y = (int)this.y;
            int x = (int)this.x;
            int z = (int)this.z;
            
            // copied from Int3D and inserted here because hashCodeFor can't be
            // inlined and this saves us a fair chunk on some hash-heavy applications
                        
            z += ~(z << 15);
            z ^=  (z >>> 10);
            z +=  (z << 3);
            z ^=  (z >>> 6);
            z += ~(z << 11);
            z ^=  (z >>> 16);
            
            z ^= y;
            z += 17;    // a little prime number shifting -- waving a dead chicken?  dunno
            
            z += ~(z << 15);
            z ^=  (z >>> 10);
            z +=  (z << 3);
            z ^=  (z >>> 6);
            z += ~(z << 11);
            z ^=  (z >>> 16);

            // nifty!  Now mix in x
            
            return x ^ z;
            }
            

        // I don't like Sun's simplistic approach to random shuffling.  So...
        // basically we need to randomly disperse <double,double,double> --> int
        // We do this by doing <double,double,double> -> <long,long,long> -> long -> int
        // The first step is done with doubleToLongBits (not RawLongBits;
        // we want all NaN to hash to the same thing).  Then conversion to
        // a single long is done by hashing (shuffling) z, then xoring it with y,
        // then hashing that and xoring with x.
        // I do that as x ^ hash(y ^ hash(z) + 17 [or whatever]). Hash function
        // taken from http://www.cris.com/~Ttwang/tech/inthash.htm

        // Some further discussion.  Sun's moved to a new hash table scheme
        // which has (of all things!) tables with lengths that are powers of two!
        // Normally hash table lengths should be prime numbers, in order to
        // compensate for bad hashcodes.  To fix matters, Sun now is
        // pre-shuffling the hashcodes with the following algorithm (which
        // is short but not too bad -- should we adopt it?  Dunno).  See
        // http://developer.java.sun.com/developer/bugParade/bugs/4669519.html
        //    key += ~(key << 9);
        //    key ^=  (key >>> 14);
        //    key +=  (key << 4);
        //    key ^=  (key >>> 10);
        // This is good for us because Int2D, Int3D, Double2D, and Double3D
        // have hashcodes well distributed with regard to y and z, but when
        // you mix in x, they're just linear in x.  We could do a final
        // shuffle I guess.  In Java 1.3, they DON'T do a pre-shuffle, so
        // it may be suboptimal.  Since we're all moving to 1.4.x, it's not
        // a big deal since 1.4.x is shuffling the final result using the
        // Sun shuffler above.  But I'd appreciate some tests on our method
        // below, and suggestions as to whether or not we should adopt the
        // shorter, likely suboptimal but faster Sun shuffler instead
        // for y and z values.  -- Sean
        
        long key = Double.doubleToLongBits(z);
        key += ~(key << 32);
        key ^= (key >>> 22);
        key += ~(key << 13);
        key ^= (key >>> 8);
        key += (key << 3);
        key ^= (key >>> 15);
        key += ~(key << 27);
        key ^= (key >>> 31);
        
        key ^= Double.doubleToLongBits(y);
        key += 17;    // a little prime number shifting -- waving a dead chicken?  dunno
        
        key += ~(key << 32);
        key ^= (key >>> 22);
        key += ~(key << 13);
        key ^= (key >>> 8);
        key += (key << 3);
        key ^= (key >>> 15);
        key += ~(key << 27);
        key ^= (key >>> 31);

        // nifty!  Now mix in z
        
        key ^= Double.doubleToLongBits(z);
        
        // Last we fold on top of each other
        return (int)(key ^ (key >> 32));
        }
        
    // can't have separate equals(...) methods as the
    // argument isn't virtual
    public boolean equals(final Object obj)
        {
        if (obj==null) return false;
        else if (obj instanceof Double3D)  // do Double3D first
            {
            Double3D other = (Double3D) obj;
            // can't just do other.x == x && other.y == y && other.z == z because we need to check for NaN
            return (Double.doubleToLongBits(other.x) == Double.doubleToLongBits(x) &&
                    Double.doubleToLongBits(other.y) == Double.doubleToLongBits(y) &&
                    Double.doubleToLongBits(other.z) == Double.doubleToLongBits(z));
            }
        else if (obj instanceof MutableDouble3D)
            {
            MutableDouble3D other = (MutableDouble3D) obj;
            // can't just do other.x == x && other.y == y && other.z == z because we need to check for NaN
            return (Double.doubleToLongBits(other.x) == Double.doubleToLongBits(x) &&
                    Double.doubleToLongBits(other.y) == Double.doubleToLongBits(y) &&
                    Double.doubleToLongBits(other.z) == Double.doubleToLongBits(z));
            }
        else if (obj instanceof Int3D)
            {
            Int3D other = (Int3D) obj;
            return (other.x == x && other.y == y && other.z == z);
            }
        else if (obj instanceof MutableInt3D)
            {
            MutableInt3D other = (MutableInt3D) obj;
            return (other.x == x && other.y == y && other.z == z);
            }
        else return false;
        }

    /** Returns the distance FROM this Double3D TO the specified point */
    public double distance(final double x, final double y, final double z)
        {
        final double dx = (double)this.x - x;
        final double dy = (double)this.y - y;
        final double dz = (double)this.z - z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
        }

    /** Returns the distance FROM this Double3D TO the specified point.   */
    public double distance(final Double3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
        }

    /** Returns the distance FROM this Double3D TO the specified point.    */
    public double distance(final Int3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
        }

    /** Returns the distance FROM this Double3D TO the specified point.    */
    public double distance(final MutableInt3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
        }

    /** Returns the squared distance FROM this Double3D TO the specified point */
    public double distanceSq(final double x, final double y, final double z)
        {
        final double dx = (double)this.x - x;
        final double dy = (double)this.y - y;
        final double dz = (double)this.z - z;
        return (dx*dx+dy*dy+dz*dz);
        }

    /** Returns the squared distance FROM this Double3D TO the specified point.    */
    public double distanceSq(final Double3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return (dx*dx+dy*dy+dz*dz);
        }

    /** Returns the squared distance FROM this Double3D TO the specified point.    */
    public double distanceSq(final Int3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return (dx*dx+dy*dy+dz*dz);
        }

    /** Returns the squared distance FROM this Double3D TO the specified point.    */
    public double distanceSq(final MutableInt3D p)
        {
        final double dx = (double)this.x - p.x;
        final double dy = (double)this.y - p.y;
        final double dz = (double)this.z - p.z;
        return (dx*dx+dy*dy+dz*dz);
        }


    /** Adds other into me, returning me. */
    public final MutableDouble3D addIn(final MutableDouble3D other)
        {
        x = other.x + x;
        y = other.y + y;
        z = other.z + z;
        return this;
        }
            
    /** Sets me to the sum of other1 and other2, returning me. */
    public final MutableDouble3D add(final MutableDouble3D other1, final MutableDouble3D other2)
        {
        x = other1.x + other2.x;
        y = other1.y + other2.y;
        z = other1.z + other2.z;
        return this;
        }
    
    /** Adds the x and y values into my x and y values, returning me. */
    public final MutableDouble3D addIn(final double x, final double y, final double z)
        {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
        }

    /** Equivalent to <tt>(new MutableDouble3D(d))</tt>, but <tt>(d.dup())</tt> shorter of course, but perhaps not quite as fast. */
    public final MutableDouble3D dup()
        {
        return new MutableDouble3D(this);
        }


    /** Sets me to me minus other, returning me.  */
    public final MutableDouble3D subtractIn(MutableDouble3D other)
        {
        x = other.x - x;
        y = other.y - y;
        z = other.z - z;
        return this;
        }

    /** Subtracts other2 from other1, setting me to the result and returning me. */
    public final MutableDouble3D subtract(MutableDouble3D other1, MutableDouble3D other2)
        {
        x = other1.x - other2.x;
        y = other1.y - other2.y;
        z = other1.z - other2.z;
        return this;
        }
            
    /** Returns the length of the vector. */
    public final double length()
        {
        return Math.sqrt(x * x + y * y + z * z);
        }
        
    /** Extends my length so that it is multiplied by val, and returns me. */
    public final MutableDouble3D multiplyIn(final double val)
        {
        x = x * val;
        y = y * val;
        z = z * val;
        return this;
        }

    /** Multiplies other by val, setting me to the result and returning me. */
    public final MutableDouble3D multiply(MutableDouble3D other, final double val)
        {
        x = other.x * val;
        y = other.y * val;
        z = other.z * val;
        return this;
        }

    /** Normalizes me (sets my length to 1.0), returning me.  Throws an error if my previous length was of length 0. */
    public final MutableDouble3D normalize()
        {
        final double invertedlen = 1.0 / Math.sqrt(x * x + y * y + z * z);
        x = x * invertedlen;
        y = y * invertedlen;
        z = z * invertedlen;
        return this;
        }
                
    /** Sets my length, which should be >= 0. */
    public final MutableDouble3D setLength(double val)
        {
        if (val < 0)
            throw new IllegalArgumentException("The argument to MutableDouble3D.setLength(...) must be zero or positive");
        if (val == 0) x = y = z = 0;
        else
            {
            final double invertedlen = val / Math.sqrt(x * x + y * y + z * z);
            x = x * invertedlen;
            y = y * invertedlen;
            z = z * invertedlen;
            }
        return this;
        } 

    /** Returns the dot product of myself against other, that is me DOT other. */
    public final double dot(MutableDouble3D other)
        {
        return other.x * x + other.y * y + other.z * z;
        }

    /** Sets the values to 0. */
    public final void zero()                                                                        
        {
        this.x = 0; 
        this.y = 0;
        this.z = 0;
        }
                
    /** Sets the values to the negation of the values in the provided MutableDouble2D */
    public final void setToMinus(final MutableDouble3D b)          
        {
        x = -b.x; 
        y = -b.y;
        z = -b.z;
        }
                
    /** Negates the MutableDouble2D's values */
    public final void negate()                                                        
        {
        x = -x;
        y = -y;
        z = -z;
        }
                
    /** Returns the square of the length of the MutableDouble2D. */
    public final double lengthSq()                                                
        {
        return x*x+y*y+z*z;
        }
    }