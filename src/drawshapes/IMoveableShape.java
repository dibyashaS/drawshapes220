package drawshapes;

public interface IMoveableShape extends IShape
{
    public void move(int dx, int dy);
    public void scale(double factor);
}
