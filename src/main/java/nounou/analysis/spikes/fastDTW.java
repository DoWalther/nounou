package nounou.analysis.spikes;

import org.eclipse.jgit.errors.StoredObjectRepresentationNotAvailableException;
import org.eclipse.jgit.lib.SymbolicRef;
import org.eclipse.jgit.util.SystemReader;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Dominik on 26.02.2016.
 */
public class fastDTW {
    public static double fastDTW(double[] template, double[] data ){
        int radius = 1;
        int min_time_size = radius + 2;
        int sizeTemplate = template.length;
        int sizeData = data.length;
        double [] template_shrinked;
        double [] data_shrinked;
        double window;
        ArrayList<Double> path;
        double distance;

        if (template.length < min_time_size || data.length < min_time_size){
            return normalDTW(template, data);
        }

        template_shrinked = reduced_by_half(template);
        data_shrinked = reduced_by_half(data);
        /*distance, path = fastDTW(template_shrinked, data_shrinked);
        window = expand_window(path, sizeTemplate, sizeData, radius);
        return normalDTW(template, data);*/
        return -1d;
    }

    public static double normalDTW(double [] template, double [] data){
        return 0d;
    }


    public static double[] reduced_by_half(double[] template){
        int modulo = template.length % 2;
        int counter = -1;
        double[] temp;

        if( modulo == 0){
            temp = new double[template.length / 2];
        }
        else{
            temp = new double[template.length / 2 + 1];
        }

        for(int i = 0; i < template.length; i += 2){
            counter += 1;
            temp[counter] = (template[i/2] + template[ 1 + i / 2])/2;
        }

        return temp;
    }

    public static double expand_window(ArrayList<Double> path, int sizeTemplate, int sizeData, int radius){



    return 0d;
    }
    public static void main(String[] args){




    }
}
