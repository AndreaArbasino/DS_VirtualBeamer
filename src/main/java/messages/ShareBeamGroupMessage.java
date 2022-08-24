package messages;

import elementsOfNetwork.BeamGroup;

public class ShareBeamGroupMessage extends Message{

    private final BeamGroup beamGroup;
    private final int id;

    public ShareBeamGroupMessage(BeamGroup beamGroup, int id) {
        this.beamGroup = beamGroup;
        this.id = id;
    }

    public BeamGroup getBeamGroup() {
        return beamGroup;
    }

    public int getId() {
        return id;
    }
}
