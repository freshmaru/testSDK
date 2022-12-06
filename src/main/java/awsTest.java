import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.internal.http.AmazonAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Iterator;
import java.util.Scanner;
public class awsTest {

    public static void main(String[] args) throws Exception {
        Region region = Region.US_EAST_1;

        Ec2Client ec2Client = Ec2Client.builder()
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
                        createInstance(ami_id);
                    break;

                case 7:
                    System.out.print("Enter instance id: ");
                    if(id_string.hasNext())
                        instance_id = id_string.nextLine();

                    if(!instance_id.isEmpty())
                        rebootInstance(ec2Client, instance_id);
                    break;

                case 8:
                    listImages();
                    break;

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

    public static void createInstance(String ami_id){

    }

    public static void rebootInstance(Ec2Client ec2Client, String instance_id){
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

    public static void listImages(){

    }

}
