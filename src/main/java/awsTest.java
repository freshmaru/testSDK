import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.internal.http.AmazonAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class awsTest {

    public static void main(String[] args) throws Exception {
        Region region = Region.US_EAST_1;

        Ec2Client ec2Client = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;

        while (true){
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones        ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("  9. condor_status               10. translation job        ");
            System.out.println("  11.list translation job        12. translate text         ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if(menu.hasNextInt()){
                number = menu.nextInt();
            }else {
                System.out.println("concentration!");
                break;
            }

            String instance_id = "";

            switch(number) {
                case 1:
                    listInstances(ec2Client);
                    break;

                case 2:
                    availableZones(ec2Client);
                    break;

                case 3:
                    System.out.print("Enter instance id: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        startInstance(ec2Client, instance_id);
                    break;

                case 4:
                    availableRegions(ec2Client);
                    break;

                case 5:
                    System.out.print("Enter instance id: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        stopInstance(ec2Client, instance_id);
                    break;

                case 6:
                    System.out.print("Enter ami id: ");
                    String ami_id = "";
                    if(id_string.hasNext())
                        ami_id = id_string.nextLine();

                    if(!ami_id.isEmpty())
                        createInstance(ec2Client, ami_id);
                    break;

                case 7:
                    System.out.print("Enter instance id: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        rebootInstance(ec2Client, instance_id);
                    break;

                case 8:
                    listImages(ec2Client);
                    break;

                case 9:
                    System.out.print("Enter publicDNS: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        condorStatus(ec2Client, instance_id);

                case 10:
                    System.out.print("Enter jobID: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        DescribeTrnaslationJob(translateClient, instance_id);
                    translateClient.close();

                case 11:
                    ListTranslationJob(translateClient);
                    translateClient.close();

                case 12:
                    System.out.print("Enter Text: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        TranslateText(translateClient, instance_id);
                    translateClient.close();


                case 99:
                    System.out.println("bye!");
                    menu.close();
                    id_string.close();
                    return;
                default: System.out.println("concentration!");
            }
        }
    }

    public static void listInstances(Ec2Client ec2Client){
        System.out.println("Listing instances...");
        String nextToken = null;
        try {
            do{
                DescribeInstancesRequest describeInstancesRequest = DescribeInstancesRequest.builder().nextToken(nextToken).build();
                DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances(describeInstancesRequest);
                for(Reservation reservation : describeInstancesResponse.reservations()){
                    for(Instance instance : reservation.instances()){
                        System.out.println("Instance Id is " + instance.instanceId());
                        System.out.println("Image id is "+ instance.imageId());
                        System.out.println("Instance type is "+ instance.instanceType());
                        System.out.println("Instance state name is "+ instance.state().name());
                        System.out.println("monitoring information is "+ instance.monitoring().state());
                    }
                }
                nextToken = describeInstancesResponse.nextToken();
            }while (nextToken!=null);
        }catch (Ec2Exception e){
            System.err.println(e.awsErrorDetails().errorCode());
            System.exit(1);
        }
    }

    public static void availableZones(Ec2Client ec2Client){
        System.out.println("Available zones...");
        DescribeAvailabilityZonesResponse zonesResponse = ec2Client.describeAvailabilityZones();
        try {
            for(AvailabilityZone zone : zonesResponse.availabilityZones()){
                System.out.printf(
                        "Found Availability Zone %s " +
                                "with status %s " +
                                "in region %s",
                        zone.zoneName(),
                        zone.state(),
                        zone.regionName());
                System.out.println();
            }
        }catch (Ec2Exception e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void startInstance(Ec2Client ec2Client, String instance_id){
        System.out.printf("Starting .... %s\n", instance_id);

        StartInstancesRequest startInstancesRequest = StartInstancesRequest.builder()
                .instanceIds(instance_id)
                .build();

        ec2Client.startInstances(startInstancesRequest);
        System.out.printf("Successfully started instance %s", instance_id);

    }

    public static void availableRegions(Ec2Client ec2Client){
        System.out.println("Available regions...");
        try{
            DescribeRegionsResponse regionsResponse = ec2Client.describeRegions();
            for(software.amazon.awssdk.services.ec2.model.Region region : regionsResponse.regions()){
                System.out.printf(
                        "Found Region %s " +
                                "with endpoint %s",
                        region.regionName(),
                        region.endpoint());
                System.out.println();
            }
        }catch (Ec2Exception e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

    public static void stopInstance(Ec2Client ec2Client, String instance_id){
        StopInstancesRequest stopInstancesRequest = StopInstancesRequest.builder()
                .instanceIds(instance_id)
                .build();

        ec2Client.stopInstances(stopInstancesRequest);
        System.out.printf("Successfully stopped instance %s", instance_id);
    }

    public static void createInstance(Ec2Client ec2Client, String ami_id){
        RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                .imageId(ami_id)
                .instanceType(InstanceType.T1_MICRO)
                .maxCount(1)
                .minCount(1)
                .build();
        RunInstancesResponse runInstancesResponse = ec2Client.runInstances(runInstancesRequest);
        String instanceId = runInstancesResponse.instances().get(0).instanceId();
        System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                instanceId, ami_id);
    }

    public static void rebootInstance(Ec2Client ec2Client, String instance_id){
        System.out.println("Rebooting...");
        try {
            RebootInstancesRequest rebootInstancesRequest = RebootInstancesRequest.builder()
                    .instanceIds(instance_id)
                    .build();

            ec2Client.rebootInstances(rebootInstancesRequest);
            System.out.printf("Successfully rebooted instance %s", instance_id);
        } catch (Ec2Exception e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void listImages(Ec2Client ec2Client){
        System.out.println("Listing images...");
        DescribeImagesResponse describeImagesResponse = ec2Client.describeImages();
        for(Image image : describeImagesResponse.images()){
            System.out.printf(
                    "Found Image ID %s " +
                            "with name %s" +
                            "owned by %s",
                    image.imageId(),
                    image.name(),
                    image.ownerId());
            System.out.println();
        }
    }

    public static void condorStatus(Ec2Client ec2Client, String publicDNS){
        System.out.println("Connecting SSH...");
        /*try {
            final String username = {username};
            final int port = {port};
            final String password = {password};
            final String privateKey = {path};

            JSch jSch = new JSch();
            Session session;
            ChannelExec channelExec;

            jSch.addIdentity(privateKey);
            System.out.println("identity added");

            session = jSch.getSession(username, publicDNS, port);
            System.out.println("Session created");
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();
            System.out.println("Session connected");

            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand("condor_status");

            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            byte[] buffer = new byte[8192];
            while (true){
                while (inputStream.available()>0){
                    int i = inputStream.read(buffer, 0, 8192);
                    if(i<0) break;
                    System.out.print(new String(buffer, 0, i));
                }
                if(channelExec.isClosed()){
                    System.out.println("exit");
                    break;
                }
            }
            channelExec.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            System.err.println("JSchException");
        } catch (IOException e) {
            System.err.println("IOException");
        }*/

    }

    public static void DescribeTrnaslationJob(TranslateClient translateClient, String id){
        try {
            DescribeTextTranslationJobRequest textTranslationJobRequest = DescribeTextTranslationJobRequest.builder()
                    .jobId(id)
                    .build();

            DescribeTextTranslationJobResponse jobResponse = translateClient.describeTextTranslationJob(textTranslationJobRequest);
            System.out.println("The job status is "+jobResponse.textTranslationJobProperties().jobStatus());
            System.out.println("The source language is "+jobResponse.textTranslationJobProperties().sourceLanguageCode());
            System.out.println("The target language is "+jobResponse.textTranslationJobProperties().targetLanguageCodes());

        } catch (TranslateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void ListTranslationJob(TranslateClient translateClient){
        try {
            ListTextTranslationJobsRequest listTextTranslationJobsRequest = ListTextTranslationJobsRequest.builder()
                    .maxResults(10)
                    .build();

            ListTextTranslationJobsResponse jobsResponse = translateClient.listTextTranslationJobs(listTextTranslationJobsRequest);
            List<TextTranslationJobProperties> properties = jobsResponse.textTranslationJobPropertiesList();
            for (TextTranslationJobProperties prop: properties){
                System.out.println("The job name is: "+prop.jobName());
                System.out.println("The Job id is: "+prop.jobId());
            }
        }catch (TranslateException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void TranslateText(TranslateClient translateClient, String translate_text){
        System.out.println("Translating Text...");

        try {
            TranslateTextRequest translateTextRequest = TranslateTextRequest.builder()
                    .sourceLanguageCode("en")
                    .targetLanguageCode("fr")
                    .text(translate_text)
                    .build();

            TranslateTextResponse translateTextResponse = translateClient.translateText(translateTextRequest);
            System.out.println(translateTextResponse.translatedText());

        } catch (TranslateException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
