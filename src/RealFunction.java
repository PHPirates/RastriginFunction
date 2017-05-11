
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;
import org.jenetics.DoubleGene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;

public class RealFunction {
// The fitness function.

    private static double fitness(final double[] x) {
        double s=0.0;
        for (int i=0; i<20; i++)
            s+= x[i]*x[i]-10*cos(2*PI*x[i]);
        return 10 * 20+s;
    }

    public static void main(final String[] args) {
        final Engine<DoubleGene, Double> engine = Engine// Create a new builder with the given fitness 
// function and chromosome
        .builder(
                RealFunction::fitness,
                codecs.ofVector(DoubleRange.of(-10.0, 10.0), 20))
        .populationSize(10000)
        .optimize(Optimize.MINIMUM)
        .alterers(
                        new Mutator<>(0.01),
                        new SinglePointCrossover<>(0.9))
                // Build an evolution engine with the
                // defined parameters.
        .offspringSelector(new RouletteWheelSelector <>())
                .build();
// Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        final Phenotype<DoubleGene, Double > best = engine.stream()
                // Truncate the evolution stream after 7 "steady"
                // generations.
                .limit(bySteadyFitness(70))
                // The evolution will stop after maximal 100
                // generations.
                .limit(1000) // Update the evaluation statistics after 
                // each generation

                .peek(statistics)
                // Collect (reduce) the evolution stream to
                // its best phenotype.
                .collect(toBestPhenotype());
        System.out.println(statistics);
        System.out.println(best);
    }
}
