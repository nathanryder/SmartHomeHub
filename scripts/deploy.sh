docker kill $(docker ps | grep "nathanryder/finalyearproject" | awk -F" " '{print $1}') &
docker pull nathanryder/finalyearproject

id=$(docker run -d nathanryder/finalyearproject)
sleep 60
running=$(docker ps | grep "nathanryder/finalyearproject")
if [ ${#running} -eq 0 ]; then
  curl -H "Content-Type: application/json" -d '{"embeds":
    [{
        "title": "Failed to deploy SmartHomeHub!",
        "color": 15158332,
        "description": "An error occured while trying to deploy into container $id"
    }]
  }' https://discord.com/api/webhooks/816398096949837874/3YXuoqv1RGbtGVHH2_TWincFEMad5_Iix6XVU6BEu9YndLe43NmhDdYKOf0BRZcknc1R
fi