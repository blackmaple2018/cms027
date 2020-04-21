package server.maps;

import client.player.Player;
import constants.MapConstants;
import java.awt.Point;
import tools.Pair;
import server.maps.object.FieldDoorObject;
import server.maps.portal.Portal;

/**
 *
 * @author Matze
 * @author Ronan
 */
public class MapleDoor {
    private int ownerId;
    private Field town;
    private Portal townPortal;
    private final Field target;
    private Pair<String, Integer> posStatus = null;
    
    private FieldDoorObject townDoor;
    private FieldDoorObject areaDoor;

    public MapleDoor(Player owner, Point targetPosition) {
        this.ownerId = owner.getId();
        this.target = owner.getMap();
        
        if (target.canDeployDoor(targetPosition)) {
            if (MapConstants.USE_ENFORCE_MDOOR_POSITION) {
                posStatus = target.getDoorPositionStatus(targetPosition);
            }
            
            if(posStatus == null) {
                this.town = this.target.getReturnField();
                this.townPortal = getDoorPortal(owner.getDoorSlot());

                if(townPortal != null) {
                    this.areaDoor = new FieldDoorObject(ownerId, town, target, false, targetPosition, townPortal.getPosition(), townPortal.getId());
                    this.townDoor = new FieldDoorObject(ownerId, target, town, true, townPortal.getPosition(), targetPosition, townPortal.getId());

                    this.areaDoor.setPairOid(this.townDoor.getObjectId());
                    this.townDoor.setPairOid(this.areaDoor.getObjectId());
                } else {
                    this.ownerId = -1;
                }
            } else {
                this.ownerId = -3;
            }
        } else {
            this.ownerId = -2;
        }
    }
    
    private Portal getDoorPortal(int slot) {
        return town.getDoorPortal(slot);
    }
    
    public int getOwnerId() {
        return ownerId;
    }

    public FieldDoorObject getTownDoor() {
        return townDoor;
    }
    
    public FieldDoorObject getAreaDoor() {
        return areaDoor;
    }
    
    public Field getTown() {
        return town;
    }

    public Portal getTownPortal() {
        return townPortal;
    }

    public Field getTarget() {
        return target;
    }

    public Pair<String, Integer> getDoorStatus() {
        return posStatus;
    }
}
