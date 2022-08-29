package messages;

import elementsOfNetwork.BeamGroup;

public class ShareBeamGroupMessage extends Message{

    private final BeamGroup beamGroup;
    private final int id;
    private final Boolean presentationStarted;

    public ShareBeamGroupMessage(BeamGroup beamGroup, int id, Boolean presentationStarted) {
        this.beamGroup = beamGroup;
        this.id = id;
        this.presentationStarted = presentationStarted;
    }

    public BeamGroup getBeamGroup() {
        return beamGroup;
    }

    public int getId() {
        return id;
    }

    public Boolean isPresentationStarted() {
        return presentationStarted;
    }
}
