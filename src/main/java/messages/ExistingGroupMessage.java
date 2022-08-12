package messages;

import ElementsOfNetwork.BeamGroup;

import java.io.Serial;

public class ExistingGroupMessage extends Message {

    @Serial
    private static final long serialVersionUID = 7736046906032042064L;

    private BeamGroup beamGroup;

    public ExistingGroupMessage(BeamGroup beamGroup) {
        this.beamGroup = beamGroup;
    }

    public BeamGroup getBeamGroup() {
        return beamGroup;
    }
}
