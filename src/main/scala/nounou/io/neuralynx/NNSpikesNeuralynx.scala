package nounou.io.neuralynx

import java.io.File

import nounou.elements.spikes.NNSpikes
import nounou.elements.traits.{NNTiming, NNTimingElement}
import nounou.io.neuralynx.headers.NNHeaderNeuralynxSpike

/**
 * Created by ktakagaki on 15/08/18.
 */
class NNSpikesNeuralynx(alignmentPoint: Int,
                        override val scaling: NNScalingNeuralynx,
                        override val timing: NNTiming,
                        val oldHeader: NNHeaderNeuralynxSpike = null)
  extends NNSpikes(alignmentPoint)
  with NNTimingElement{


}
