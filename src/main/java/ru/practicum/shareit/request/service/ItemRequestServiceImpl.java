package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestResponse createItemRequest(CreateItemRequestDto createItemRequestDto, Integer userId) {
        User user = findUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(createItemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestResponse(itemRequest);
    }

    public List<ItemRequestWithAnswersResponse> getUserItemRequests(Integer userId) {
        findUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<Integer> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Integer, List<Item>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return itemRequests.stream()
                .map(request -> {
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
                    return ItemRequestMapper.mapToItemRequestWithAnswersResponse(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getAllItemRequests(Integer userId) {
        findUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::mapToItemRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithAnswersResponse getItemRequestById(Integer requestId, Integer userId) {
        findUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with ID: " + requestId));
        List<Item> itemsByRequestId = itemRepository.findByRequestId(requestId);
        return ItemRequestMapper.mapToItemRequestWithAnswersResponse(itemRequest, itemsByRequestId);
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }
}
