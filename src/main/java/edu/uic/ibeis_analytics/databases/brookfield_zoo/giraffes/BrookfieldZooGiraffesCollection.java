package edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes;

import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisIndividual;
import edu.uic.ibeis_java_api.api.individual.IndividualNotes;
import edu.uic.ibeis_java_api.api.individual.Size;
import edu.uic.ibeis_java_api.api.individual.Weight;
import edu.uic.ibeis_java_api.exceptions.IndividualNameAlreadyExistsException;
import edu.uic.ibeis_java_api.exceptions.MalformedHttpRequestException;
import edu.uic.ibeis_java_api.exceptions.UnsuccessfulHttpRequestException;
import edu.uic.ibeis_java_api.values.ConservationStatus;
import edu.uic.ibeis_java_api.values.LengthUnitOfMeasure;
import edu.uic.ibeis_java_api.values.Sex;
import edu.uic.ibeis_java_api.values.WeightUnitOfMeasure;

import java.io.IOException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class BrookfieldZooGiraffesCollection {

    public enum BrookfieldZooGiraffesDbNames {
        ARNIETA("Arnieta"), FRANNY("Franny"), JASIRI("Jasiri"), MITHRA("Mithra"), POTOKA("Potoka"), OTHER("Other"), ZEBRA("Zebra");

        private String value;

        BrookfieldZooGiraffesDbNames(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private Ibeis ibeis = new Ibeis();

    private IbeisIndividual arnieta;
    private IbeisIndividual franny;
    private IbeisIndividual jasiri;
    private IbeisIndividual mithra;
    private IbeisIndividual potoka;
    private IbeisIndividual zebra;
    private IbeisIndividual other;

    public BrookfieldZooGiraffesCollection() {

        try {
            //Arnieta
            try {
                arnieta = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.ARNIETA.getValue());
                arnieta.setSex(Sex.FEMALE);
                IndividualNotes ArnietaNotes = new IndividualNotes();
                ArnietaNotes.setDescription("Arnieta is the most social with behind the scenes tours and guests. " +
                        "This \"Mamma\'s Girl\" likes to know where her mother, Franny, is at all times.");
                ArnietaNotes.setLocation("Brookfield Zoo");
                ArnietaNotes.setHabitat("Habitat Africa! The Savannah");
                ArnietaNotes.setConservationStatus(ConservationStatus.LC);
                ArnietaNotes.setDateOfBirth(new GregorianCalendar(2005, 7, 12).getTime());
                ArnietaNotes.setSize(new Size(13.5, LengthUnitOfMeasure.FOOT));
                ArnietaNotes.setWeight(new Weight(1860, WeightUnitOfMeasure.POUND));
                ArnietaNotes.setDiet("Alfalfa hay, grain, chopped carrots, sweet potatoes, apples, and bread;" +
                        " willow and maple browse when available.");
                arnieta.setIndividualNotes(ArnietaNotes);
            } catch (IndividualNameAlreadyExistsException e) {
                arnieta = findIndividual(BrookfieldZooGiraffesDbNames.ARNIETA.getValue());
            }

            //Franny
            try {
                franny = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.FRANNY.getValue());
                franny.setSex(Sex.FEMALE);
                IndividualNotes FrannyNotes = new IndividualNotes();
                FrannyNotes.setDescription("Franny likes to stand out in the rain. She will wait for the weight of the water" +
                        " to lower leaves so she can reach them. She is the least social giraffe with her keepers.");
                FrannyNotes.setLocation("Brookfield Zoo");
                FrannyNotes.setHabitat("Habitat Africa! The Savannah");
                FrannyNotes.setConservationStatus(ConservationStatus.LC);
                FrannyNotes.setDateOfBirth(new GregorianCalendar(1991, 7, 21).getTime());
                FrannyNotes.setSize(new Size(14.5, LengthUnitOfMeasure.FOOT));
                FrannyNotes.setWeight(new Weight(1760, WeightUnitOfMeasure.POUND));
                FrannyNotes.setDiet("Alfalfa hay, grain, chopped carrots, sweet potatoes, apples, and bread;" +
                        " willow and maple browse when available.");
                franny.setIndividualNotes(FrannyNotes);
            } catch (IndividualNameAlreadyExistsException e) {
                franny = findIndividual(BrookfieldZooGiraffesDbNames.FRANNY.getValue());
            }

            //Jasiri
            try {
                jasiri = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.JASIRI.getValue());
                jasiri.setSex(Sex.FEMALE);
                IndividualNotes JasiriNotes = new IndividualNotes();
                JasiriNotes.setDescription("Jasiri, mother to Potaka, hates to be rained on." +
                        " She will ask to come inside when it is raining." +
                        " She investigates items on the ground more than the other giraffes." +
                        " Her nickname is Jazzy.");
                JasiriNotes.setLocation("Brookfield Zoo");
                JasiriNotes.setHabitat("Habitat Africa! The Savannah");
                JasiriNotes.setConservationStatus(ConservationStatus.LC);
                JasiriNotes.setDateOfBirth(new GregorianCalendar(2005, 7, 12).getTime());
                JasiriNotes.setSize(new Size(13, LengthUnitOfMeasure.FOOT));
                JasiriNotes.setWeight(new Weight(1860, WeightUnitOfMeasure.POUND));
                JasiriNotes.setDiet("Alfalfa hay, grain, chopped carrots, sweet potatoes, apples, and bread;" +
                        " willow and maple browse when available.");
                jasiri.setIndividualNotes(JasiriNotes);
            } catch (IndividualNameAlreadyExistsException e) {
                jasiri = findIndividual(BrookfieldZooGiraffesDbNames.JASIRI.getValue());
            }

            //Mithra
            try {
                mithra = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.MITHRA.getValue());
                mithra.setSex(Sex.FEMALE);
                IndividualNotes MithraNotes = new IndividualNotes();
                MithraNotes.setDescription("Mithra is our most gentile giraffe. She has a very sweet disposition, expressive face, ears." +
                        " She, also, seems to forget the tortoises over each winter and is re-surprised by them every spring.");
                MithraNotes.setLocation("Brookfield Zoo");
                MithraNotes.setHabitat("Habitat Africa! The Savannah");
                MithraNotes.setConservationStatus(ConservationStatus.LC);
                MithraNotes.setDateOfBirth(new GregorianCalendar(1990, 5, 23).getTime());
                MithraNotes.setSize(new Size(14, LengthUnitOfMeasure.FOOT));
                MithraNotes.setWeight(new Weight(1694, WeightUnitOfMeasure.POUND));
                MithraNotes.setDiet("Alfalfa hay, grain, chopped carrots, sweet potatoes, apples, and bread;" +
                        " willow and maple browse when available.");
                mithra.setIndividualNotes(MithraNotes);
            } catch (IndividualNameAlreadyExistsException e) {
                mithra = findIndividual(BrookfieldZooGiraffesDbNames.MITHRA.getValue());
            }
            //Potoka
            try {
                potoka = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.POTOKA.getValue());
                potoka.setSex(Sex.FEMALE);
                IndividualNotes PotokaNotes = new IndividualNotes();
                PotokaNotes.setDescription("Potoka is our youngest and fastest growing giraffe." +
                        " This laid back male can be frequently seen \"splay-legged\" grazing on grass in his outdoor habitat.");
                PotokaNotes.setLocation("Brookfield Zoo");
                PotokaNotes.setHabitat("Habitat Africa! The Savannah");
                PotokaNotes.setConservationStatus(ConservationStatus.LC);
                PotokaNotes.setDateOfBirth(new GregorianCalendar(2013, 6, 21).getTime());
                PotokaNotes.setSize(new Size(12, LengthUnitOfMeasure.FOOT));
                PotokaNotes.setWeight(new Weight(1320, WeightUnitOfMeasure.POUND));
                PotokaNotes.setDiet("Alfalfa hay, grain, chopped carrots, sweet potatoes, apples, and bread;" +
                        " willow and maple browse when available.");
                potoka.setIndividualNotes(PotokaNotes);
            } catch (IndividualNameAlreadyExistsException e) {
                potoka = findIndividual(BrookfieldZooGiraffesDbNames.POTOKA.getValue());
            }

            //Other
            try {
                other = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.OTHER.getValue());
            } catch (IndividualNameAlreadyExistsException e) {
                other = findIndividual(BrookfieldZooGiraffesDbNames.OTHER.getValue());
            }

            //Zebra
            try {
                zebra = ibeis.addIndividual(BrookfieldZooGiraffesDbNames.ZEBRA.getValue());
            } catch (IndividualNameAlreadyExistsException e) {
                zebra = findIndividual(BrookfieldZooGiraffesDbNames.ZEBRA.getValue());
            }

        } catch (IOException | MalformedHttpRequestException | UnsuccessfulHttpRequestException e) {
            e.printStackTrace();
        }
    }

    private IbeisIndividual findIndividual(String name) {
        try {
            List<IbeisIndividual> allIndividuals = ibeis.getAllIndividuals();
            for (IbeisIndividual individual : allIndividuals) {
                if (individual.getName().equals(name)) {
                    return individual;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IbeisIndividual> getIndividuals() {
        return Arrays.asList(arnieta,franny,jasiri,mithra,potoka,other,zebra);
    }
}
