package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;

public class StatusStructure {
    private StatusEntry root;

    private HeightedPoint reference;
    private int size = 0;

    public StatusStructure(HeightedPoint reference) {
        this.reference = reference;
    }

    public void insert(HeightedPoint p) { // TODO: was tun wir mit Punkten mit undefinierter Höhe?
        double key = reference.calcDistance(p);
        double slope = reference.calcSlope(p);
        StatusEntry y = null;
        StatusEntry x = root;
        while (x != null) {
            y = x;
            y.maxSlope = Math.max(y.maxSlope, slope);
            if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        StatusEntry z = new StatusEntry(key, p, slope, y);
        if (y == null) {
            root = z;
        } else {
            if (key < y.key) {
                y.left = z;
            } else {
                y.right = z;
            }
        }
        fixAfterInsertion(z);
        size++;
        //assert checkStatusStructure(root);
    }

    public void delete(HeightedPoint pt) {
        StatusEntry p = getEntry(pt);
        if (p == null)
            return;
        assert p.slope <= p.maxSlope : "p.maxSlope is too low";

        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        // Because of (p.right != null) the successor of p is the minimum of p.right 
        if (p.left != null && p.right != null) {
            StatusEntry s = getMinimum(p.right); // = successor(p)
            p.key = s.key;
            p.value = s.value;
            p.slope = s.slope;
            p = s;
        } // p has 2 children
        
        // update maxSlope
        p.maxSlope = Math.max(maxSlopeOf(p.left), maxSlopeOf(p.right)); // dummy value
        StatusEntry x = p.parent;
        while (x != null) {
            x.maxSlope = Math.max( x.slope, Math.max(maxSlopeOf(x.left), maxSlopeOf(x.right)) );
            x = x.parent;
        }

        // Start fixup at replacement node, if it exists.
        StatusEntry replacement = (p.left != null ? p.left : p.right);

        if (replacement != null) {
            // Here p has exactly one child. Otherwise p would point to its successor, which has no left child.
            // Link replacement to parent
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = p.right = p.parent = null;

            // Fix replacement
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // return if we are the only node.
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink.
            if (p.color == BLACK)
                fixAfterDeletion(p);

            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
        //assert checkStatusStructure(root);
    }

    public boolean isVisible(HeightedPoint pt) {
        double key = reference.calcDistance(pt);
        StatusEntry p = null;
        StatusEntry x = root;
        double maxSlope = Double.NEGATIVE_INFINITY;
        while (x != null && ! pt.equalsPosition(x.value)) {
            p = x;
            if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
                maxSlope = Math.max(maxSlope, Math.max(maxSlopeOf(p.left), p.slope));
            }
        }
        if (x == null) { // didn't find pt
            return false;
        }
        maxSlope = Math.max(maxSlope, maxSlopeOf(x.left));
        return maxSlope < x.slope; // TODO: use <= instead?!
    }

    /**
     * Searches the status structure for a point p
     * and returns the corresponding StatusEntry.
     * @param p HeightedPoint to be searched
     * @return StatusEntry
     */
    StatusEntry getEntry(HeightedPoint p) {
        double key = reference.calcDistance(p);
        StatusEntry t = root;
        while (t != null) {
            if (key < t.key) {
                t = t.left;
            } else if (key > t.key) {
                t = t.right;
            } else if (p.equalsPosition(t.value)) {
                return t; // found it!
            } else {
                //search to the left and to the right
                if (t.left != null && p.equalsPosition(t.left.value)) {
                    return t.left;
                }
                if (t.right != null && p.equalsPosition(t.right.value)) {
                    return t.right;
                }
                return null; // assuming the searched point can only be in one of the children
            }
        }
        return null;
    }
    StatusEntry getMinimum(StatusEntry p) {
        if (p == null) {
            return null;
        }
        StatusEntry min = p;
        while (min.left != null) {
            min = min.left;
        }
        return min;
    }

    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    static final class StatusEntry {
        double key; // distance to reference point
        HeightedPoint value;
        double maxSlope;
        double slope;
        StatusEntry left;
        StatusEntry right;
        StatusEntry parent;
        boolean color = RED;

        StatusEntry(double key, HeightedPoint value, double slope, StatusEntry parent) {
            this.key = key;
            this.value = value;
            this.maxSlope = slope;
            this.slope = slope;
            this.parent = parent;
        }
        
        public double getKey() {
            return key;
        }
        public HeightedPoint getValue() {
            return value;
        }
        public double getMaxSlope() {
            return maxSlope;
        }

        @Override
        public String toString() {
            return key + "=" + value + "(" + maxSlope + ")";
        }
    }

    private static double maxSlopeOf(StatusEntry p) {
        return (p == null ? Double.NEGATIVE_INFINITY: p.maxSlope);
    }
    private static boolean colorOf(StatusEntry p) {
        return (p == null ? BLACK : p.color);
    }
    private static void setColor(StatusEntry p, boolean c) {
        if (p != null)
            p.color = c;
    }
    private static StatusEntry parentOf(StatusEntry p) {
        return (p == null ? null: p.parent);
    }
    private static StatusEntry leftOf(StatusEntry p) {
        return (p == null) ? null: p.left;
    }
    private static StatusEntry rightOf(StatusEntry p) {
        return (p == null) ? null: p.right;
    }

    private void rotateLeft(StatusEntry p) { // p = x; y = r;
        if (p != null) {
            StatusEntry r = p.right;
            p.right = r.left; // TODO: make sure r is not null?!
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
            
            // augmenting
            r.maxSlope = p.maxSlope;
            p.maxSlope = Math.max( p.slope, Math.max(maxSlopeOf(p.left), maxSlopeOf(p.right)) );
        }
    }
    private void rotateRight(StatusEntry p) {
        if (p != null) {
            StatusEntry l = p.left;
            p.left = l.right; // TODO: make sure l is not null?!
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
            
            // augmenting
            l.maxSlope = p.maxSlope;
            p.maxSlope = Math.max( p.slope, Math.max(maxSlopeOf(p.left), maxSlopeOf(p.right)) );
        }
    }

    private void fixAfterInsertion(StatusEntry x) {
        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                StatusEntry y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {
                StatusEntry y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }
    private void fixAfterDeletion(StatusEntry x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                StatusEntry sib = rightOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                if (colorOf(leftOf(sib))  == BLACK &&
                    colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else { // symmetric
                StatusEntry sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                    colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }
    private static boolean checkStatusStructure(StatusEntry x) {
        if (x == null) return true;
        if (x.maxSlope != Math.max(x.slope, Math.max(maxSlopeOf(x.left), maxSlopeOf(x.right)))) {
            return false;
        }
        return checkStatusStructure(x.left) && checkStatusStructure(x.right);
    }
}