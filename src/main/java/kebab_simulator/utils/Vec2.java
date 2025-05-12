package kebab_simulator.utils;

import com.sun.javafx.geom.Vec2f;

public class Vec2 extends com.sun.javafx.geom.Vec2d {

    public Vec2() {}

    public Vec2(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public Vec2(Vec2 var1) {
        this.set(var1);
    }

    public Vec2(com.sun.javafx.geom.Vec2d var1) {
        this.set(var1);
    }

    public Vec2(Vec2f var1) {
        this.set(var1);
    }

    public Vec2 set(Double var1, Double var2) {
        if (var1 != null) this.x = var1;
        if (var2 != null) this.y = var2;
        return this;
    }

    public Vec2 add(Vec2 other) {
        if (other != null) {
            this.x += other.x;
            this.y += other.y;
        }
        return this;
    }

    public Vec2 add(Double x, Double y) {
        if (x != null) this.x += x;
        if (y != null) this.y += y;
        return this;
    }

    public Vec2 sub(Vec2 other) {
        if (other != null) {
            this.x -= other.x;
            this.y -= other.y;
        }
        return this;
    }

    public Vec2 sub(Double x, Double y) {
        if (x != null) this.x -= x;
        if (y != null) this.y -= y;
        return this;
    }

    public Vec2 mul(Vec2 other) {
        if (other != null) {
            this.x *= other.x;
            this.y *= other.y;
        }
        return this;
    }

    public Vec2 mul(Double x, Double y) {
        if (x != null) this.x *= x;
        if (y != null) this.y *= y;
        return this;
    }

    public Vec2 div(Vec2 other) {
        if (other != null) {
            this.x /= other.x;
            this.y /= other.y;
        }
        return this;
    }

    public Vec2 div(Double x, Double y) {
        if (x != null) this.x /= x;
        if (y != null) this.y /= y;
        return this;
    }

    public double rawLen() {
        return this.x * this.x + this.y * this.y;
    }

    public double len() {
        return Math.sqrt(this.rawLen());
    }

    public Vec2 normalizeImplaced() {
        double l = this.len();
        if (l > 0) {
            this.x = this.x / l;
            this.y = this.y / l;
        }
        return this;
    }

    public Vec2 normalized() {
        double l = this.len();
        double x = 0, y = 0;
        if (l > 0) {
            x = this.x / l;
            y = this.y / l;
        }
        return new Vec2(x, y);
    }

    public double dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }
}
