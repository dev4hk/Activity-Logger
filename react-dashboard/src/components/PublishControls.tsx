import React from 'react';
import {ACTIVITY_TYPES, type UserActivityRequest} from "../types";

interface PublishControlsProps {
    onPublishRandom: (eventData: UserActivityRequest) => void;
    onRefreshAll: () => void;
}

const PublishControls: React.FC<PublishControlsProps> = ({ onPublishRandom, onRefreshAll }) => {
    const handleGenerateAndPublishRandom = () => {
        const randomUserId = crypto.randomUUID();
        const randomActivityType = ACTIVITY_TYPES[Math.floor(Math.random() * ACTIVITY_TYPES.length)];
        const detailsOptions = ["Homepage", "ProductX", "CategoryY", "Item123", "Order456", "ShippingAddress"];
        const randomDetails = detailsOptions[Math.floor(Math.random() * detailsOptions.length)];

        const eventData: UserActivityRequest = {
            userId: randomUserId,
            activityType: randomActivityType,
            details: randomDetails
        };
        onPublishRandom(eventData);
    };

    return (
        <div className="flex flex-col sm:flex-row justify-center items-center gap-4 mb-8">
            <button
                onClick={handleGenerateAndPublishRandom}
                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 dark:bg-blue-700 dark:hover:bg-blue-600"
            >
                Publish Random Event
            </button>
            <button
                onClick={onRefreshAll}
                className="bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 dark:bg-green-700 dark:hover:bg-green-600"
            >
                Refresh All Events (REST Fallback)
            </button>
        </div>
    );
};

export default PublishControls;
